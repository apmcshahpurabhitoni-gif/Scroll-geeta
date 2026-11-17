const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_geeta_123";
const BOB_UID = "bob_geeta_456";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user cannot read or write any user collections", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  const past = new Date(Date.now() - 1000);

  await assertFails(unauthDb.collection(`users/${ALICE_UID}/bookmarks`).get());
  await assertFails(unauthDb.collection(`users/${ALICE_UID}/reflections`).get());
  await assertFails(unauthDb.collection(`users/${ALICE_UID}/progress`).get());
  await assertFails(unauthDb.collection(`users/${ALICE_UID}/read_verses`).get());

  await assertFails(
    unauthDb.collection(`users/${ALICE_UID}/bookmarks`).doc("BG2_47").set({
      userId: ALICE_UID,
      verseId: "BG2_47",
      chapter: 2,
      verse: 47,
      chapterTitle: "Sankhya Yoga",
      sanskritPreview: "कर्मण्येवाधिकारस्ते",
      hindiPreview: "कर्म करने में ही तुम्हारा अधिकार है",
      englishPreview: "You have a right to perform your prescribed duty",
      createdAt: past,
    })
  );
});

test("Owner can create, list, and delete bookmarks; shadow fields and @DocumentId id key fail", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const past = new Date(Date.now() - 2000);

  await assertSucceeds(
    aliceDb.collection(`users/${ALICE_UID}/bookmarks`).doc("BG2_47").set({
      userId: ALICE_UID,
      verseId: "BG2_47",
      chapter: 2,
      verse: 47,
      chapterTitle: "Sankhya Yoga",
      sanskritPreview: "कर्मण्येवाधिकारस्ते मा फलेषु कदाचन।",
      hindiPreview: "तेरा कर्म करने में ही अधिकार है, फल में कभी नहीं।",
      englishPreview: "Thy right is to work only, but never to its fruits.",
      createdAt: past,
    })
  );

  await assertSucceeds(aliceDb.collection(`users/${ALICE_UID}/bookmarks`).get());

  // Reject extra @DocumentId 'id' key
  await assertFails(
    aliceDb.collection(`users/${ALICE_UID}/bookmarks`).doc("BG2_48").set({
      id: "BG2_48",
      userId: ALICE_UID,
      verseId: "BG2_48",
      chapter: 2,
      verse: 48,
      chapterTitle: "Sankhya Yoga",
      sanskritPreview: "योगस्थः कुरु कर्माणि",
      hindiPreview: "योग में स्थित होकर कर्म कर",
      englishPreview: "Perform action steadfast in yoga",
      createdAt: past,
    })
  );

  // Reject future timestamp
  await assertFails(
    aliceDb.collection(`users/${ALICE_UID}/bookmarks`).doc("BG2_49").set({
      userId: ALICE_UID,
      verseId: "BG2_49",
      chapter: 2,
      verse: 49,
      chapterTitle: "Sankhya Yoga",
      sanskritPreview: "दूरेण ह्यवरं कर्म",
      hindiPreview: "समबुद्धि से कर्म",
      englishPreview: "Action is far inferior to yoga of wisdom",
      createdAt: new Date(Date.now() + 60000),
    })
  );

  await assertSucceeds(
    aliceDb.collection(`users/${ALICE_UID}/bookmarks`).doc("BG2_47").delete()
  );
});

test("Owner can create, update, list, and delete reflections; invalid moodTag or immutable edit fails", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const created = new Date(Date.now() - 5000);

  await assertSucceeds(
    aliceDb.collection(`users/${ALICE_UID}/reflections`).doc("note_1").set({
      userId: ALICE_UID,
      verseId: "BG2_47",
      chapter: 2,
      verse: 47,
      noteText: "Focus on sincere effort today rather than anxiety over results.",
      moodTag: "Clarity",
      createdAt: created,
      updatedAt: created,
    })
  );

  // Valid partial update
  await assertSucceeds(
    aliceDb.collection(`users/${ALICE_UID}/reflections`).doc("note_1").update({
      noteText: "Re-read this morning: detachment brings calm focus.",
      moodTag: "Peace",
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Invalid moodTag fails
  await assertFails(
    aliceDb.collection(`users/${ALICE_UID}/reflections`).doc("note_1").update({
      moodTag: "InvalidMood",
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Immutable createdAt mutation fails
  await assertFails(
    aliceDb.collection(`users/${ALICE_UID}/reflections`).doc("note_1").update({
      createdAt: new Date(Date.now() - 500),
      updatedAt: new Date(Date.now() - 500),
    })
  );

  await assertSucceeds(
    aliceDb.collection(`users/${ALICE_UID}/reflections`).get()
  );
  await assertSucceeds(
    aliceDb.collection(`users/${ALICE_UID}/reflections`).doc("note_1").delete()
  );
});

test("Owner can create and update reading progress and mark read_verses", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const created = new Date(Date.now() - 5000);

  await assertSucceeds(
    aliceDb.collection(`users/${ALICE_UID}/progress`).doc("summary").set({
      userId: ALICE_UID,
      lastChapter: 2,
      lastVerse: 47,
      currentStreak: 1,
      longestStreak: 1,
      totalVersesRead: 1,
      lastReadDate: "2026-11-17",
      createdAt: created,
      updatedAt: created,
    })
  );

  await assertSucceeds(
    aliceDb.collection(`users/${ALICE_UID}/progress`).doc("summary").update({
      lastChapter: 2,
      lastVerse: 48,
      currentStreak: 2,
      longestStreak: 2,
      totalVersesRead: 2,
      lastReadDate: "2026-11-18",
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  await assertSucceeds(
    aliceDb.collection(`users/${ALICE_UID}/read_verses`).doc("BG2_47").set({
      userId: ALICE_UID,
      verseId: "BG2_47",
      chapter: 2,
      verse: 47,
      createdAt: created,
    })
  );

  await assertSucceeds(aliceDb.collection(`users/${ALICE_UID}/read_verses`).get());
});

test("Cross-user isolation: Alice cannot access or mutate Bob's data", async () => {
  const past = new Date(Date.now() - 5000);
  await testEnv.withSecurityRulesDisabled(async (context) => {
    const db = context.firestore();
    await db.collection(`users/${BOB_UID}/bookmarks`).doc("BG1_1").set({
      userId: BOB_UID,
      verseId: "BG1_1",
      chapter: 1,
      verse: 1,
      chapterTitle: "Arjuna Visada Yoga",
      sanskritPreview: "धर्मक्षेत्रे कुरुक्षेत्रे",
      hindiPreview: "धर्मक्षेत्र कुरुक्षेत्र में",
      englishPreview: "In the sacred plain of Kurukshetra",
      createdAt: past,
    });
    await db.collection(`users/${BOB_UID}/reflections`).doc("bob_note").set({
      userId: BOB_UID,
      verseId: "BG1_1",
      chapter: 1,
      verse: 1,
      noteText: "Bob private note",
      moodTag: "Duty",
      createdAt: past,
      updatedAt: past,
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection(`users/${BOB_UID}/bookmarks`).get());
  await assertFails(aliceDb.collection(`users/${BOB_UID}/bookmarks`).doc("BG1_1").get());
  await assertFails(aliceDb.collection(`users/${BOB_UID}/reflections`).get());
  await assertFails(
    aliceDb.collection(`users/${BOB_UID}/reflections`).doc("bob_note").update({
      noteText: "Hijacked",
      updatedAt: new Date(Date.now() - 1000),
    })
  );
  await assertFails(
    aliceDb.collection(`users/${BOB_UID}/bookmarks`).doc("BG1_1").delete()
  );
});
