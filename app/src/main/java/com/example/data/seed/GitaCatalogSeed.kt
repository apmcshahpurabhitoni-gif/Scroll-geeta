package com.example.data.seed

import com.example.model.ChapterSummary
import com.example.model.DailyVerseInfo
import com.example.model.ExplainerPerspective
import com.example.model.KrishnaScene
import com.example.model.Shloka
import com.example.model.VerseImportanceTier
import com.example.model.VerseRanking
import com.example.model.verseKey

object GitaCatalogSeed {

    val chapters: List<ChapterSummary> = listOf(
        ChapterSummary(1, "अर्जुनविषादयोग", "Arjuna Viṣāda Yoga", "Arjuna's Dilemma", "The Yoga of Arjuna's Dejection", "Standing between two armies on Kurukshetra, Arjuna is overwhelmed by sorrow and moral conflict before Lord Krishna begins His counsel.", 47, KrishnaScene.KURUKSHETRA),
        ChapterSummary(2, "सांख्ययोग", "Sāṅkhya Yoga", "Transcendental Knowledge", "The Yoga of Wisdom", "Krishna reveals the immortality of the soul (Atman), equanimity in pleasure and pain, and the cornerstone teaching of Nishkama Karma.", 72, KrishnaScene.KURUKSHETRA),
        ChapterSummary(3, "कर्मयोग", "Karma Yoga", "The Path of Selfless Action", "The Yoga of Action", "Why sincere action performed as duty without selfish attachment leads to inner freedom and sets an example for the world.", 43, KrishnaScene.KARMA),
        ChapterSummary(4, "ज्ञानकर्मसंन्यासयोग", "Jñāna Karma Sanyāsa Yoga", "Wisdom in Action", "The Yoga of Knowledge & Renunciation", "Krishna explains the divine descent of Dharma across ages and how wisdom turns ordinary action into sacred offering.", 42, KrishnaScene.KARMA),
        ChapterSummary(5, "कर्मसंन्यासयोग", "Karma Sanyāsa Yoga", "Renunciation Through Action", "The Yoga of Renunciation", "Reconciling outer action with inner detachment: seeing the same serene Spirit dwelling equally in all beings.", 29, KrishnaScene.KARMA),
        ChapterSummary(6, "ध्यानयोग", "Dhyāna Yoga", "The Path of Meditation", "The Yoga of Self-Mastery", "Practical guidance for steadying the restless mind through balanced living, meditation, and compassionate clarity.", 47, KrishnaScene.DHYANA),
        ChapterSummary(7, "ज्ञानविज्ञानयोग", "Jñāna Vijñāna Yoga", "Knowledge & Realization", "The Yoga of Wisdom & Experience", "Understanding the Supreme as the unifying essence woven through earth, water, fire, intelligence, and devotion.", 30, KrishnaScene.DHYANA),
        ChapterSummary(8, "अक्षरब्रह्मयोग", "Akṣara Brahma Yoga", "The Imperishable Absolute", "The Yoga of the Eternal", "How steady remembrance shapes consciousness throughout life and guides the soul toward the imperishable goal.", 28, KrishnaScene.DHYANA),
        ChapterSummary(9, "राजविद्याराजगुह्ययोग", "Rāja Vidyā Rāja Guhya Yoga", "Sovereign Secret", "The Yoga of Royal Knowledge", "The most direct mystery of devotion: whatever is offered with a pure, loving heart is warmly received by the Divine.", 34, KrishnaScene.VISHWAROOPA),
        ChapterSummary(10, "विभूतियोग", "Vibhūti Yoga", "Divine Manifestations", "The Yoga of Splendor", "Wherever greatness, beauty, strength, or wisdom shines in creation, see a spark of the Infinite.", 42, KrishnaScene.VISHWAROOPA),
        ChapterSummary(11, "विश्वरूपदर्शनयोग", "Viśvarūpa Darśana Yoga", "The Universal Form", "The Yoga of the Cosmic Vision", "Krishna grants Arjuna divine sight to behold the boundless Cosmic Form containing all universes, time, and creation.", 55, KrishnaScene.VISHWAROOPA),
        ChapterSummary(12, "भक्तियोग", "Bhakti Yoga", "The Yoga of Devotion", "The Path of Loving Devotion", "The qualities dearest to Krishna: compassion, forgiveness, fearlessness, and an unshakeable loving heart.", 20, KrishnaScene.VISHWAROOPA),
        ChapterSummary(13, "क्षेत्रक्षेत्रज्ञविभागयोग", "Kṣetra Kṣetrajña Vibhāga Yoga", "The Field & Its Knower", "Distinction of Body and Spirit", "Distinguishing the ever-changing field of body and mind from the silent, witnessing Knower within.", 35, KrishnaScene.DHYANA),
        ChapterSummary(14, "गुणत्रयविभागयोग", "Guṇatraya Vibhāga Yoga", "The Three Gunas", "Qualities of Material Nature", "How Sattva (harmony), Rajas (passion), and Tamas (inertia) shape our habits, and how awareness rises above them.", 27, KrishnaScene.DHYANA),
        ChapterSummary(15, "पुरुषोत्तमयोग", "Puruṣottama Yoga", "The Supreme Spirit", "The Yoga of the Supreme Person", "The inverted cosmic Ashvattha tree metaphor and Krishna's declaration as the indwelling light of memory and wisdom.", 20, KrishnaScene.VISHWAROOPA),
        ChapterSummary(16, "दैवासुरसम्पद्विभागयोग", "Daivāsura Sampad Vibhāga Yoga", "Divine & Restless Natures", "Cultivating Noble Virtues", "Contrasting fearless, truthful, compassionate tendencies with ego-driven pride, anger, and greed.", 24, KrishnaScene.KARMA),
        ChapterSummary(17, "श्रद्धात्रयविभागयोग", "Śraddhātraya Vibhāga Yoga", "Threefold Faith", "Faith in Food, Worship & Giving", "How our quiet inner faith expresses itself in everyday speech, generosity, discipline, and nourishment.", 28, KrishnaScene.KARMA),
        ChapterSummary(18, "मोक्षसंन्यासयोग", "Mokṣa Sanyāsa Yoga", "Liberation & Surrender", "The Final Synthesis", "The grand culmination of the Gita: performing one's Swadharma with surrender, peace, and unwavering trust.", 78, KrishnaScene.KURUKSHETRA)
    )

    val dailyVerses: List<DailyVerseInfo> = listOf(
        DailyVerseInfo(2, 47, "Focus on Effort, Release Anxiety", "Where in your life today can you give your wholehearted effort without worrying about the outcome?"),
        DailyVerseInfo(2, 48, "Equanimity in Action", "How can you remain balanced and steady whether today brings praise or challenge?"),
        DailyVerseInfo(6, 5, "Be Your Own Steadfast Friend", "What small habit today strengthens your inner discipline rather than pulling you down?"),
        DailyVerseInfo(12, 13, "Compassionate Equanimity", "Think of someone who tests your patience—can you respond with quiet kindness today?"),
        DailyVerseInfo(3, 19, "Duty Without Attachment", "Approach your main responsibility today as a calm offering rather than a burden."),
        DailyVerseInfo(4, 7, "Renewal of Dharma", "Notice where integrity and courage are needed in your own circle today."),
        DailyVerseInfo(9, 22, "Trust & Devotion", "Reflect on what truly sustains you when your mind rests in gratitude."),
        DailyVerseInfo(18, 66, "Peaceful Surrender", "What unnecessary fear can you lay down today so you can move forward lightly?"),
        DailyVerseInfo(6, 35, "Training the Restless Mind", "Pause for three calm breaths today whenever distraction pulls your attention away."),
        DailyVerseInfo(5, 18, "Equal Vision", "Look past surface labels today and recognize the shared spark in everyone you meet.")
    )

    fun sceneForVerse(chapter: Int, verse: Int): KrishnaScene {
        if (chapter == 11) return KrishnaScene.VISHWAROOPA
        if (chapter == 12) return KrishnaScene.VISHWAROOPA
        if (chapter == 2 && verse in 54..72) return KrishnaScene.DHYANA
        return chapters.firstOrNull { it.chapterNumber == chapter }?.scene ?: KrishnaScene.KURUKSHETRA
    }

    fun speakerForVerse(chapter: Int, verse: Int): String {
        return when {
            chapter == 1 && verse == 1 -> "Dhritarashtra asks Sanjaya • Kurukshetra"
            chapter == 1 && verse in 2..20 -> "Sanjaya narrates to Dhritarashtra • Kurukshetra"
            chapter == 1 && verse >= 21 -> "Arjuna speaks amidst both armies • Kurukshetra"
            chapter == 2 && verse in 1..10 -> "Arjuna & Sri Krishna • Chariot Counsel"
            chapter == 11 && verse in 1..4 -> "Arjuna requests the Universal Form"
            chapter == 11 && verse in 5..14 -> "Sri Krishna reveals the Cosmic Vision"
            chapter == 11 && verse in 15..31 -> "Arjuna beholds the Vishwaroopa"
            chapter == 18 && verse in 74..78 -> "Sanjaya concludes the sacred dialogue"
            else -> "Sri Krishna counsels Arjuna • Chapter $chapter"
        }
    }

    fun getSeedShloka(chapter: Int, verse: Int): Shloka? = seedMap[verseKey(chapter, verse)]

    val allSeedShlokas: List<Shloka> by lazy { seedMap.values.toList() }

    private val curatedRankings: Map<String, VerseRanking> = mapOf(
        "BG1_1" to VerseRanking(90, VerseImportanceTier.CORE_ESSENCE, false, "Opening question on Dharmakshetra Kurukshetra setting the entire dialogue in motion."),
        "BG2_11" to VerseRanking(93, VerseImportanceTier.CORE_ESSENCE, true, "Krishna begins His formal philosophical counsel right after Arjuna surrenders in 2.7-2.10."),
        "BG2_13" to VerseRanking(96, VerseImportanceTier.CORE_ESSENCE, false, "Foundational teaching on the unchanging embodied Self across childhood, youth, and old age."),
        "BG2_20" to VerseRanking(96, VerseImportanceTier.CORE_ESSENCE, true, "Continues the immortality of Atman teaching (2.19-2.22): unborn, eternal, and indestructible."),
        "BG2_22" to VerseRanking(96, VerseImportanceTier.CORE_ESSENCE, false, "Classic analogy of changing worn garments for new ones to explain reincarnation."),
        "BG2_47" to VerseRanking(100, VerseImportanceTier.CORE_ESSENCE, false, "Cornerstone verse of Nishkama Karma Yoga: full dedication to action without clinging to results."),
        "BG2_48" to VerseRanking(98, VerseImportanceTier.CORE_ESSENCE, true, "Immediately expands on 2.47 by defining Yoga itself as equanimity (Samatvam) in success and failure."),
        "BG2_55" to VerseRanking(92, VerseImportanceTier.CORE_ESSENCE, true, "Directly answers Arjuna's question in 2.54 about how a person of steady wisdom (Sthitaprajna) lives."),
        "BG2_62" to VerseRanking(91, VerseImportanceTier.CORE_ESSENCE, true, "First half of the psychological ladder showing how brooding leads to attachment and anger (completed in 2.63)."),
        "BG2_63" to VerseRanking(91, VerseImportanceTier.CORE_ESSENCE, true, "Completes 2.62's chain from anger to delusion, loss of memory, and spiritual ruin."),
        "BG3_19" to VerseRanking(94, VerseImportanceTier.CORE_ESSENCE, true, "Concludes Krishna's argument in 3.17-3.18: act selflessly without attachment to attain the Supreme."),
        "BG3_21" to VerseRanking(94, VerseImportanceTier.CORE_ESSENCE, true, "Builds on 3.20 (King Janaka's example) to show how leaders set the ethical standard for society."),
        "BG4_7" to VerseRanking(97, VerseImportanceTier.CORE_ESSENCE, true, "Paired directly with 4.8: Krishna's promise to manifest whenever Dharma declines."),
        "BG4_8" to VerseRanking(97, VerseImportanceTier.CORE_ESSENCE, true, "Grammatically and thematically completes 4.7, stating the threefold purpose of divine incarnation."),
        "BG4_18" to VerseRanking(91, VerseImportanceTier.CORE_ESSENCE, false, "Profound paradox of Karma Yoga: seeing stillness within action and purpose within restraint."),
        "BG5_18" to VerseRanking(94, VerseImportanceTier.CORE_ESSENCE, false, "Celebrated declaration of equal spiritual vision across all beings."),
        "BG6_5" to VerseRanking(95, VerseImportanceTier.CORE_ESSENCE, true, "Paired with 6.6: elevate yourself by your own mind; the mind is your friend or enemy."),
        "BG6_6" to VerseRanking(90, VerseImportanceTier.CORE_ESSENCE, true, "Directly clarifies 6.5 by explaining when the mind acts as an ally versus an adversary."),
        "BG6_35" to VerseRanking(94, VerseImportanceTier.CORE_ESSENCE, true, "Directly answers Arjuna's lament in 6.34 about the restless mind being as hard to tame as the wind."),
        "BG7_7" to VerseRanking(93, VerseImportanceTier.CORE_ESSENCE, true, "Summarizes 7.4-7.6: all lower and higher nature rests in the Divine like pearls on a thread."),
        "BG8_6" to VerseRanking(93, VerseImportanceTier.CORE_ESSENCE, true, "Expands 8.5's teaching on final remembrance and flows into 8.7's practical guidance."),
        "BG9_22" to VerseRanking(95, VerseImportanceTier.CORE_ESSENCE, false, "Krishna's direct assurance of Yogakshema—carrying the welfare of single-hearted devotees."),
        "BG9_26" to VerseRanking(93, VerseImportanceTier.CORE_ESSENCE, false, "A leaf, flower, fruit, or water offered with pure love is joyfully accepted by the Divine."),
        "BG10_20" to VerseRanking(93, VerseImportanceTier.CORE_ESSENCE, true, "Opens the Vibhuti enumeration (10.20-10.42) in answer to Arjuna's request in 10.16-10.18."),
        "BG11_32" to VerseRanking(95, VerseImportanceTier.CORE_ESSENCE, true, "Answers Arjuna's awe-struck question in 11.31 ('Who are You in this fierce form?')."),
        "BG12_13" to VerseRanking(95, VerseImportanceTier.CORE_ESSENCE, true, "Begins the famous portrait of a dear devotee continued seamlessly through 12.14."),
        "BG13_23" to VerseRanking(92, VerseImportanceTier.CORE_ESSENCE, true, "Culminates the Kshetra-Kshetrajna analysis by naming the indwelling Witness (Upadrashta)."),
        "BG14_26" to VerseRanking(92, VerseImportanceTier.CORE_ESSENCE, true, "Answers Arjuna's question in 14.21 on how one transcends the three Gunas."),
        "BG15_15" to VerseRanking(95, VerseImportanceTier.CORE_ESSENCE, false, "Krishna declares Himself seated in every heart as the source of memory, knowledge, and resolution of doubt."),
        "BG16_1" to VerseRanking(91, VerseImportanceTier.CORE_ESSENCE, true, "First verse of a 3-verse sentence (16.1-16.3) enumerating divine qualities beginning with fearlessness."),
        "BG17_3" to VerseRanking(92, VerseImportanceTier.CORE_ESSENCE, true, "Answers Arjuna's opening inquiry (17.1) by revealing how inner Shraddha shapes one's whole being."),
        "BG18_63" to VerseRanking(90, VerseImportanceTier.CORE_ESSENCE, true, "Krishna respects Arjuna's free will ('reflect fully and act as you choose') right before 18.64-66."),
        "BG18_66" to VerseRanking(99, VerseImportanceTier.CORE_ESSENCE, true, "Charama Shloka: crowns Krishna's final counsel in 18.64-65 with unconditional refuge and freedom from sorrow."),
        "BG18_78" to VerseRanking(95, VerseImportanceTier.CORE_ESSENCE, true, "Sanjaya's closing benediction completing the narrative arc opened in 1.1 and 18.74-77.")
    )

    fun verseRankingFor(chapter: Int, verse: Int): VerseRanking {
        curatedRankings[verseKey(chapter, verse)]?.let { return it }

        val chSummary = chapters.firstOrNull { it.chapterNumber == chapter }
        val maxVerses = chSummary?.versesCount ?: 40

        return when {
            chapter == 1 && verse in 2..20 -> VerseRanking(
                score = 48,
                tier = VerseImportanceTier.SUPPORTING_CONTEXT,
                requiresAdjacentContext = true,
                contextSummary = "Part of Sanjaya's sequential roll-call of warriors and conch blasts; requires surrounding verses for full narrative flow."
            )
            chapter == 10 && verse in 21..38 -> VerseRanking(
                score = 68,
                tier = VerseImportanceTier.SUPPORTING_CONTEXT,
                requiresAdjacentContext = true,
                contextSummary = "Continues Krishna's enumeration of divine manifestations begun in 10.20."
            )
            chapter == 11 && verse in 15..31 -> VerseRanking(
                score = 76,
                tier = VerseImportanceTier.KEY_INSIGHT,
                requiresAdjacentContext = true,
                contextSummary = "Part of Arjuna's continuous awed description of the Vishwaroopa leading to Krishna's reply in 11.32."
            )
            chapter == 12 && verse in 14..20 -> VerseRanking(
                score = 86,
                tier = VerseImportanceTier.KEY_INSIGHT,
                requiresAdjacentContext = true,
                contextSummary = "Continues the portrait of the ideal devotee introduced in BG 12.13."
            )
            chapter == 2 && verse in 54..72 -> VerseRanking(
                score = 85,
                tier = VerseImportanceTier.KEY_INSIGHT,
                requiresAdjacentContext = true,
                contextSummary = "Part of the Sthitaprajna (steady wisdom) dialogue triggered by Arjuna's question in 2.54."
            )
            verse == 1 || verse == maxVerses -> VerseRanking(
                score = 82,
                tier = VerseImportanceTier.KEY_INSIGHT,
                requiresAdjacentContext = true,
                contextSummary = if (verse == 1) {
                    "Opening verse of Chapter $chapter that bridges from the preceding chapter's conclusion."
                } else {
                    "Closing pivot verse of Chapter $chapter preparing the transition into Chapter ${chapter + 1}."
                }
            )
            chapter in setOf(2, 3, 4, 6, 9, 12, 18) -> VerseRanking(
                score = 78,
                tier = VerseImportanceTier.KEY_INSIGHT,
                requiresAdjacentContext = true,
                contextSummary = "Part of Krishna's step-by-step discourse in Chapter $chapter; check preceding and following verses for the complete logical arc."
            )
            else -> VerseRanking(
                score = 64,
                tier = VerseImportanceTier.SUPPORTING_CONTEXT,
                requiresAdjacentContext = true,
                contextSummary = "Supporting teaching or dialogue link in Chapter $chapter best understood alongside adjacent verses."
            )
        }
    }

    private fun Shloka.withRanking(): Shloka {
        val ranking = verseRankingFor(chapter, verse)
        return copy(
            importanceScore = ranking.score,
            importanceTier = ranking.tier,
            requiresAdjacentContext = ranking.requiresAdjacentContext,
            contextSummary = ranking.contextSummary
        )
    }

    private val seedMap: Map<String, Shloka> = listOf(
        Shloka(
            id = "BG1_1",
            chapter = 1,
            verse = 1,
            speakerContext = "Dhritarashtra asks Sanjaya • Kurukshetra",
            sanskrit = "धृतराष्ट्र उवाच |\nधर्मक्षेत्रे कुरुक्षेत्रे समवेता युयुत्सवः |\nमामकाः पाण्डवाश्चैव किमकुर्वत सञ्जय ||१-१||",
            transliteration = "dhṛtarāṣṭra uvāca |\ndharmakṣetre kurukṣetre samavetā yuyutsavaḥ |\nmāmakāḥ pāṇḍavāścaiva kimakurvata sañjaya ||1-1||",
            hindi = "धृतराष्ट्र बोले — हे सञ्जय! धर्मभूमि कुरुक्षेत्र में एकत्रित युद्ध की इच्छा वाले मेरे और पाण्डु के पुत्रों ने क्या किया?",
            english = "Dhritarashtra said: O Sanjaya, assembled together on the sacred plain of Kurukshetra and eager for battle, what did my sons and the sons of Pandu do?",
            explainers = listOf(
                ExplainerPerspective(
                    scholarName = "Swami Sivananda",
                    tradition = "Vedantic Purport",
                    languageLabel = "English",
                    content = "Kurukshetra is called Dharmakshetra—the field of righteousness—signifying that inner moral conflict always unfolds on the sacred ground of conscience. Notice Dhritarashtra's attachment in 'my sons and Pandu's sons,' which sparks the entire Gita."
                ),
                ExplainerPerspective(
                    scholarName = "Swami Tejomayananda",
                    tradition = "Chinmaya Mission",
                    languageLabel = "हिन्दी",
                    content = "कुरुक्षेत्र केवल एक भौगोलिक युद्धभूमि नहीं, बल्कि हमारे भीतर चलने वाले सद्गुण और मोह के संघर्ष का प्रतीक है, जहाँ 'मेरे और पराए' का भेद ही विषाद का मूल कारण बनता है।"
                )
            ),
            scene = KrishnaScene.KURUKSHETRA
        ),
        Shloka(
            id = "BG2_13",
            chapter = 2,
            verse = 13,
            speakerContext = "Sri Krishna counsels Arjuna • Chariot Counsel",
            sanskrit = "देहिनोऽस्मिन्यथा देहे कौमारं यौवनं जरा |\nतथा देहान्तरप्राप्तिर्धीरस्तत्र न मुह्यति ||२-१३||",
            transliteration = "dehino'sminyathā dehe kaumāraṃ yauvanaṃ jarā |\ntathā dehāntaraprāptirdhīrastatra na muhyati ||2-13||",
            hindi = "जैसे जीवात्मा की इस देह में बालपन, जवानी और वृद्धावस्था होती है, वैसे ही अन्य शरीर की प्राप्ति होती है; इसमें धीर पुरुष मोहित नहीं होता।",
            english = "Just as the embodied soul experiences childhood, youth, and old age in this body, so too does it pass into another body; the steadfast wise person is not bewildered by this.",
            explainers = listOf(
                ExplainerPerspective(
                    scholarName = "Swami Sivananda",
                    tradition = "Vedantic Purport",
                    languageLabel = "English",
                    content = "Your body changes continuously from infancy to age, yet the witnessing awareness ('I') remains unbroken. Knowing yourself as that unchanging witness dissolves fear of change and loss."
                ),
                ExplainerPerspective(
                    scholarName = "Swami Ramsukhdas",
                    tradition = "Gita Sadhak Sanjeevani",
                    languageLabel = "हिन्दी",
                    content = "शरीर की अवस्थाएँ बदलती रहती हैं पर शरीरी (आत्मा) सदा एकरस रहता है। इस नित्य सत्य को समझने वाला धीर व्यक्ति शोक और मोह से ऊपर उठ जाता है।"
                )
            ),
            scene = KrishnaScene.KURUKSHETRA
        ),
        Shloka(
            id = "BG2_47",
            chapter = 2,
            verse = 47,
            speakerContext = "Sri Krishna counsels Arjuna • Nishkama Karma",
            sanskrit = "कर्मण्येवाधिकारस्ते मा फलेषु कदाचन |\nमा कर्मफलहेतुर्भूर्मा ते सङ्गोऽस्त्वकर्मणि ||२-४७||",
            transliteration = "karmaṇyevādhikāraste mā phaleṣu kadācana |\nmā karmaphalaheturbhūrmā te saṅgo'stvakarmaṇi ||2-47||",
            hindi = "तेरा कर्म करने में ही अधिकार है, उसके फलों में कभी नहीं। इसलिए तू कर्मों के फल का हेतु मत बन और तेरी अकर्मण्यता (कर्म न करने) में भी आसक्ति न हो।",
            english = "You have a right to perform your prescribed action alone, never to the fruits of action. Never let the reward be your motive, nor be attached to inaction.",
            explainers = listOf(
                ExplainerPerspective(
                    scholarName = "Swami Sivananda",
                    tradition = "Vedantic Purport",
                    languageLabel = "English",
                    content = "This is the golden rule of Karma Yoga. Anxiety arises when the mind chases uncontrollable future outcomes instead of pouring full excellence into the present action. Detachment from results does not mean laziness—Krishna explicitly warns against inaction."
                ),
                ExplainerPerspective(
                    scholarName = "Swami Chinmayananda",
                    tradition = "The Holy Geeta",
                    languageLabel = "English",
                    content = "The present moment is entirely yours to shape through action; the fruit belongs to the larger cosmic order. Acting with full sincerity and zero anxiety unlocks peak clarity and peace."
                ),
                ExplainerPerspective(
                    scholarName = "Swami Tejomayananda",
                    tradition = "Chinmaya Mission",
                    languageLabel = "हिन्दी",
                    content = "मनुष्य का अधिकार केवल कर्म करने तक सीमित है। फल की चिंता छोड़कर पूरी निष्ठा से कर्तव्य निभाना ही सच्चा कर्मयोग है, और आलस्य या पलायन भी त्याज्य है।"
                )
            ),
            scene = KrishnaScene.KURUKSHETRA
        ),
        Shloka(
            id = "BG2_48",
            chapter = 2,
            verse = 48,
            speakerContext = "Sri Krishna counsels Arjuna • Equanimity",
            sanskrit = "योगस्थः कुरु कर्माणि सङ्गं त्यक्त्वा धनञ्जय |\nसिद्ध्यसिद्ध्योः समो भूत्वा समत्वं योग उच्यते ||२-४८||",
            transliteration = "yogasthaḥ kuru karmāṇi saṅgaṃ tyaktvā dhanañjaya |\nsiddhyasiddhyoḥ samo bhūtvā samatvaṃ yoga ucyate ||2-48||",
            hindi = "हे धनञ्जय! आसक्ति को त्यागकर तथा सिद्धि और असिद्धि में समान भाव रखकर योग में स्थित हुआ कर्म कर; समत्व (मन की समता) ही योग कहलाता है।",
            english = "Perform action, O Arjuna, steadfast in yoga, abandoning selfish attachment and remaining even-minded in success and failure; for equanimity of mind is called Yoga.",
            explainers = listOf(
                ExplainerPerspective(
                    scholarName = "Swami Sivananda",
                    tradition = "Vedantic Purport",
                    languageLabel = "English",
                    content = "Krishna gives one of the clearest definitions of Yoga: Samatvam—inner balance amidst outer wins and setbacks. When you stay centered, failure teaches you and success stays humble."
                ),
                ExplainerPerspective(
                    scholarName = "Swami Ramsukhdas",
                    tradition = "Gita Sadhak Sanjeevani",
                    languageLabel = "हिन्दी",
                    content = "सफलता में हर्षित न होना और असफलता में विषाद न करना — यही समता रूप योग है जो हर कर्म को बंधन से मुक्त कर देता है।"
                )
            ),
            scene = KrishnaScene.KURUKSHETRA
        ),
        Shloka(
            id = "BG3_19",
            chapter = 3,
            verse = 19,
            speakerContext = "Sri Krishna counsels Arjuna • Karma Yoga",
            sanskrit = "तस्मादसक्तः सततं कार्यं कर्म समाचर |\nअसक्तो ह्याचरन्कर्म परमाप्नोति पूरुषः ||३-१९||",
            transliteration = "tasmādasaktaḥ satataṃ kāryaṃ karma samācara |\nasakto hyācarankarma paramāpnoti pūruṣaḥ ||3-19||",
            hindi = "इसलिए तू निरन्तर आसक्ति से रहित होकर सदा कर्तव्य-कर्म को भलीभाँति कर; क्योंकि आसक्तिरहित होकर कर्म करता हुआ मनुष्य परमात्मा को प्राप्त हो जाता है।",
            english = "Therefore, always perform your rightful duty without attachment; for by working without selfish attachment, a person attains the Supreme.",
            explainers = listOf(
                ExplainerPerspective(
                    scholarName = "Swami Sivananda",
                    tradition = "Vedantic Purport",
                    languageLabel = "English",
                    content = "Everyday work itself becomes a spiritual path when freed from egoic clinging. You do not need to flee your responsibilities to realize truth—transform how you carry them."
                )
            ),
            scene = KrishnaScene.KARMA
        ),
        Shloka(
            id = "BG3_21",
            chapter = 3,
            verse = 21,
            speakerContext = "Sri Krishna counsels Arjuna • Leadership & Example",
            sanskrit = "यद्यदाचरति श्रेष्ठस्तत्तदेवेतरो जनः |\nस यत्प्रमाणं कुरुते लोकस्तदनुवर्तते ||३-२१||",
            transliteration = "yadyadācarati śreṣṭhastattadevetaro janaḥ |\nsa yatpramāṇaṃ kurute lokastadanuvartate ||3-21||",
            hindi = "श्रेष्ठ पुरुष जो-जो आचरण करता है, अन्य व्यक्ति भी वैसा ही आचरण करते हैं। वह जो कुछ प्रमाण बना देता है, समस्त संसार उसी के अनुसार चलता है।",
            english = "Whatever a noble leader does, others follow; whatever standard they set by their example, the world walks in those footsteps.",
            explainers = listOf(
                ExplainerPerspective(
                    scholarName = "Swami Chinmayananda",
                    tradition = "The Holy Geeta",
                    languageLabel = "English",
                    content = "True leadership is lived integrity, not speeches. Our quiet choices set the moral climate for our family, community, and team."
                )
            ),
            scene = KrishnaScene.KARMA
        ),
        Shloka(
            id = "BG4_7",
            chapter = 4,
            verse = 7,
            speakerContext = "Sri Krishna reveals His divine mission",
            sanskrit = "यदा यदा हि धर्मस्य ग्लानिर्भवति भारत |\nअभ्युत्थानमधर्मस्य तदात्मानं सृजाम्यहम् ||४-७||",
            transliteration = "yadā yadā hi dharmasya glānirbhavati bhārata |\nabhyutthānamadharmasya tadātmānaṃ sṛjāmyaham ||4-7||",
            hindi = "हे भारत! जब-जब धर्म की हानि और अधर्म की वृद्धि होती है, तब-तब ही मैं अपने रूप को प्रकट करता हूँ।",
            english = "Whenever righteousness declines and unrighteousness rises, O Bharata, then I manifest Myself.",
            explainers = listOf(
                ExplainerPerspective(
                    scholarName = "Swami Sivananda",
                    tradition = "Vedantic Purport",
                    languageLabel = "English",
                    content = "A timeless promise of cosmic renewal: whenever truth and harmony fade, divine grace intervenes—both in history and in the awakening of an individual heart."
                )
            ),
            scene = KrishnaScene.KARMA
        ),
        Shloka(
            id = "BG4_8",
            chapter = 4,
            verse = 8,
            speakerContext = "Sri Krishna reveals His divine mission",
            sanskrit = "परित्राणाय साधूनां विनाशाय च दुष्कृताम् |\nधर्मसंस्थापनार्थाय सम्भवामि युगे युगे ||४-८||",
            transliteration = "paritrāṇāya sādhūnāṃ vināśāya ca duṣkṛtām |\ndharmasaṃsthāpanārthāya sambhavāmi yuge yuge ||4-8||",
            hindi = "साधु पुरुषों की रक्षा के लिए, दुष्कर्मियों के विनाश के लिए और धर्म की भलीभाँति स्थापना करने के लिए मैं युग-युग में प्रकट होता हूँ।",
            english = "For the protection of the virtuous, for the transformation of wrongdoing, and for establishing righteousness firmly, I appear age after age.",
            explainers = listOf(
                ExplainerPerspective(
                    scholarName = "Swami Tejomayananda",
                    tradition = "Chinmaya Mission",
                    languageLabel = "हिन्दी",
                    content = "अवतार का उद्देश्य सज्जनों का संरक्षण, दुर्बुद्धि का निवारण और समाज में धर्म की मर्यादा की पुनर्स्थापना करना है।"
                )
            ),
            scene = KrishnaScene.KARMA
        ),
        Shloka(
            id = "BG5_18",
            chapter = 5,
            verse = 18,
            speakerContext = "Sri Krishna counsels Arjuna • Equal Vision",
            sanskrit = "विद्याविनयसम्पन्ने ब्राह्मणे गवि हस्तिनि |\nशुनि चैव श्वपाके च पण्डिताः समदर्शिनः ||५-१८||",
            transliteration = "vidyāvinayasampanne brāhmaṇe gavi hastini |\nśuni caiva śvapāke ca paṇḍitāḥ samadarśinaḥ ||5-18||",
            hindi = "ज्ञानीजन विद्या और विनय से युक्त ब्राह्मण में तथा गाय, हाथी, कुत्ते और चाण्डाल में भी समदर्शी (एक ही ब्रह्म को देखने वाले) होते हैं।",
            english = "The truly wise look with equal vision upon a learned and humble scholar, a cow, an elephant, a dog, and an outcast—seeing the same Spirit in all.",
            explainers = listOf(
                ExplainerPerspective(
                    scholarName = "Swami Sivananda",
                    tradition = "Vedantic Purport",
                    languageLabel = "English",
                    content = "True wisdom dissolves social prejudice and arrogance. Recognizing one shared consciousness across all beings naturally produces deep humility and universal kindness."
                )
            ),
            scene = KrishnaScene.KARMA
        ),
        Shloka(
            id = "BG6_5",
            chapter = 6,
            verse = 5,
            speakerContext = "Sri Krishna counsels Arjuna • Self-Mastery",
            sanskrit = "उद्धरेदात्मनात्मानं नात्मानमवसादयेत् |\nआत्मैव ह्यात्मनो बन्धुरात्मैव रिपुरात्मनः ||६-५||",
            transliteration = "uddharedātmanātmānaṃ nātmānamavasādayet |\nātmaiva hyātmano bandhurātmaiva ripurātmanaḥ ||6-5||",
            hindi = "मनुष्य को चाहिए कि वह अपने आप से अपना उद्धार करे और अपने को नीचे न गिराए; क्योंकि यह जीवात्मा स्वयं ही अपना मित्र है और स्वयं ही अपना शत्रु है।",
            english = "Let a person lift oneself by one's own mind, and never degrade oneself; for the mind alone is one's friend, and the mind alone is one's enemy.",
            explainers = listOf(
                ExplainerPerspective(
                    scholarName = "Swami Sivananda",
                    tradition = "Vedantic Purport",
                    languageLabel = "English",
                    content = "Empowering inner accountability: no external circumstance controls your growth as much as your own disciplined thoughts. Train the mind patiently to become your strongest ally."
                )
            ),
            scene = KrishnaScene.DHYANA
        ),
        Shloka(
            id = "BG6_35",
            chapter = 6,
            verse = 35,
            speakerContext = "Sri Krishna answers Arjuna on taming the restless mind",
            sanskrit = "असंशयं महाबाहो मनो दुर्निग्रहं चलम् |\nअभ्यासेन तु कौन्तेय वैराग्येण च गृह्यते ||६-३५||",
            transliteration = "asaṃśayaṃ mahābāho mano durnigrahaṃ calam |\nabhyāsena tu kaunteya vairāgyeṇa ca gṛhyate ||6-35||",
            hindi = "हे महाबाहो! निःसन्देह मन चञ्चल और कठिनता से वश में होने वाला है; परन्तु हे कुन्तीपुत्र! यह अभ्यास और वैराग्य से वश में हो जाता है।",
            english = "Undoubtedly, O mighty-armed Arjuna, the mind is restless and difficult to restrain; yet through steady practice and gentle detachment, it can indeed be mastered.",
            explainers = listOf(
                ExplainerPerspective(
                    scholarName = "Swami Chinmayananda",
                    tradition = "The Holy Geeta",
                    languageLabel = "English",
                    content = "Krishna never dismisses how hard focus feels—He validates Arjuna's struggle and gives the twin antidote: Abhyasa (consistent daily practice) and Vairagya (stepping back from distractions)."
                )
            ),
            scene = KrishnaScene.DHYANA
        ),
        Shloka(
            id = "BG9_22",
            chapter = 9,
            verse = 22,
            speakerContext = "Sri Krishna promises unwavering care to devotees",
            sanskrit = "अनन्याश्चिन्तयन्तो मां ये जनाः पर्युपासते |\nतेषां नित्याभियुक्तानां योगक्षेमं वहाम्यहम् ||९-२२||",
            transliteration = "ananyāścintayanto māṃ ye janāḥ paryupāsate |\nteṣāṃ nityābhiyuktānāṃ yogakṣemaṃ vahāmyaham ||9-22||",
            hindi = "जो अनन्य भक्त मेरा चिन्तन करते हुए निष्काम भाव से मेरी उपासना करते हैं, उन नित्य मुझसे जुड़े हुए भक्तों का योगक्षेम (आवश्यकताओं की पूर्ति व रक्षा) मैं स्वयं वहन करता हूँ।",
            english = "To those who meditate on Me with undivided heart and steadfast devotion, I personally carry the responsibility for their welfare and security.",
            explainers = listOf(
                ExplainerPerspective(
                    scholarName = "Swami Sivananda",
                    tradition = "Vedantic Purport",
                    languageLabel = "English",
                    content = "Yoga means bringing what is needed; Kshema means safeguarding what you already have. When your heart is anchored in sincerity and devotion, fear of scarcity melts away."
                )
            ),
            scene = KrishnaScene.VISHWAROOPA
        ),
        Shloka(
            id = "BG11_32",
            chapter = 11,
            verse = 32,
            speakerContext = "Sri Krishna speaks in the Cosmic Vishwaroopa",
            sanskrit = "कालोऽस्मि लोकक्षयकृत्प्रवृद्धो\nलोकान्समाहर्तुमिह प्रवृत्तः |\nऋतेऽपि त्वां न भविष्यन्ति सर्वे\nयेऽवस्थिताः प्रत्यनीकेषु योधाः ||११-३२||",
            transliteration = "kālo'smi lokakṣayakṛtpravṛddho\nlokānsamāhartumiha pravṛttaḥ |\nṛte'pi tvāṃ na bhaviṣyanti sarve\nye'vasthitāḥ pratyanīkeṣu yodhāḥ ||11-32||",
            hindi = "मैं लोकों का नाश करने वाला बढ़ा हुआ महाकाल हूँ और इस समय इन सब लोकों का संहार करने के लिए प्रवृत्त हुआ हूँ; तुम्हारे बिना भी प्रतिपक्षी सेनाओं में स्थित ये सब योद्धा जीवित नहीं रहेंगे।",
            english = "I am mighty Time, the great transformer of worlds, engaged here in withdrawing all beings; even without you, none of the warriors arrayed in opposing ranks shall remain.",
            explainers = listOf(
                ExplainerPerspective(
                    scholarName = "Swami Sivananda",
                    tradition = "Vedantic Purport",
                    languageLabel = "English",
                    content = "Krishna reveals that cosmic history is governed by Time (Kala), humbling the human ego that imagines it controls everything. Arjuna is called to be a clear, courageous instrument of Dharma."
                )
            ),
            scene = KrishnaScene.VISHWAROOPA
        ),
        Shloka(
            id = "BG12_13",
            chapter = 12,
            verse = 13,
            speakerContext = "Sri Krishna describes the devotee dear to Him",
            sanskrit = "अद्वेष्टा सर्वभूतानां मैत्रः करुण एव च |\nनिर्ममो निरहङ्कारः समदुःखसुखः क्षमी ||१२-१३||",
            transliteration = "adveṣṭā sarvabhūtānāṃ maitraḥ karuṇa eva ca |\nnirmamo nirahaṅkāraḥ samaduḥkhasukhaḥ kṣamī ||12-13||",
            hindi = "जो किसी भी प्राणी से द्वेष नहीं करता, सबका मित्र और करुणामय है, ममता और अहंकार से रहित, सुख-दुःख में समान और क्षमाशील है — वह मुझे प्रिय है।",
            english = "One who bears ill-will toward no being, who is friendly and compassionate to all, free from possessiveness and ego, balanced in joy and sorrow, and ever forgiving—such a devotee is dear to Me.",
            explainers = listOf(
                ExplainerPerspective(
                    scholarName = "Swami Chinmayananda",
                    tradition = "The Holy Geeta",
                    languageLabel = "English",
                    content = "True spirituality shows up in character: kindness without hatred, compassion in action, freedom from ego, and graceful forgiveness."
                )
            ),
            scene = KrishnaScene.VISHWAROOPA
        ),
        Shloka(
            id = "BG18_66",
            chapter = 18,
            verse = 66,
            speakerContext = "Sri Krishna's crowning assurance • Moksha Sanyasa",
            sanskrit = "सर्वधर्मान्परित्यज्य मामेकं शरणं व्रज |\nअहं त्वां सर्वपापेभ्यो मोक्षयिष्यामि मा शुचः ||१८-६६||",
            transliteration = "sarvadharmānparityajya māmekaṃ śaraṇaṃ vraja |\nahaṃ tvāṃ sarvapāpebhyo mokṣayiṣyāmi mā śucaḥ ||18-66||",
            hindi = "सम्पूर्ण धर्मों (कर्तव्यों के अभिमान) को त्यागकर तू केवल एक मेरी शरण में आ जा। मैं तुझे सम्पूर्ण पापों से मुक्त कर दूँगा, तू शोक मत कर।",
            english = "Abandoning all narrow clinging to lesser duties, take refuge in Me alone. I shall liberate you from all sorrow and past burdens—do not grieve.",
            explainers = listOf(
                ExplainerPerspective(
                    scholarName = "Swami Sivananda",
                    tradition = "Vedantic Purport",
                    languageLabel = "English",
                    content = "Known as the Charama Shloka (ultimate verse): after laying out Knowledge, Action, Meditation, and Devotion, Krishna invites complete trust and release from guilt or fear."
                ),
                ExplainerPerspective(
                    scholarName = "Swami Ramsukhdas",
                    tradition = "Gita Sadhak Sanjeevani",
                    languageLabel = "हिन्दी",
                    content = "भगवान का यह अभयदान है कि निश्चल शरणागति से समस्त बंधन और शोक स्वतः मिट जाते हैं — 'मा शुचः' (शोक मत कर) पूरी गीता का करुणामय सार है।"
                )
            ),
            scene = KrishnaScene.KURUKSHETRA
        ),
        Shloka(
            id = "BG7_7",
            chapter = 7,
            verse = 7,
            speakerContext = "Sri Krishna reveals the unifying thread of existence",
            sanskrit = "मत्तः परतरं नान्यत्किञ्चिदस्ति धनञ्जय |\nमयि सर्वमिदं प्रोतं सूत्रे मणिगणा इव ||७-७||",
            transliteration = "mattaḥ parataraṃ nānyatkiñcidasti dhanañjaya |\nmayi sarvamidaṃ protaṃ sūtre maṇigaṇā iva ||7-7||",
            hindi = "हे धनञ्जय! मुझसे श्रेष्ठ दूसरा कोई कारण नहीं है। यह सम्पूर्ण जगत् सूत्र में मणियों के सदृश मुझमें गुँथा हुआ है।",
            english = "There is nothing higher than Me, O Arjuna. All this universe is strung upon Me, like clusters of pearls upon a thread.",
            explainers = listOf(
                ExplainerPerspective(
                    scholarName = "Swami Sivananda",
                    tradition = "Vedantic Purport",
                    languageLabel = "English",
                    content = "Just as one invisible thread holds together separate pearls into a harmonious necklace, one divine Consciousness unites all diverse beings and experiences."
                )
            ),
            scene = KrishnaScene.DHYANA
        ),
        Shloka(
            id = "BG8_6",
            chapter = 8,
            verse = 6,
            speakerContext = "Sri Krishna explains how steady thoughts shape our destiny",
            sanskrit = "यं यं वापि स्मरन्भावं त्यजत्यन्ते कलेवरम् |\nतं तमेवैति कौन्तेय सदा तद्भावभावितः ||८-६||",
            transliteration = "yaṃ yaṃ vāpi smaranbhāvaṃ tyajatyante kalevaram |\ntaṃ tamevaiti kaunteya sadā tadbhāvabhāvitaḥ ||8-6||",
            hindi = "हे कुन्तीपुत्र! मनुष्य अन्तकाल में जिस-जिस भी भाव का स्मरण करता हुआ शरीर को त्यागता है, सदा उसी भाव से भावित होता हुआ वह उसी को प्राप्त होता है।",
            english = "Whatever state of being one remembers when leaving the body at the end, to that very state one attains, O Arjuna, because of constant absorption in that thought.",
            explainers = listOf(
                ExplainerPerspective(
                    scholarName = "Swami Chinmayananda",
                    tradition = "The Holy Geeta",
                    languageLabel = "English",
                    content = "What occupies our mind in the final moment is simply the harvest of what we practice daily. Cultivate luminous thoughts now, each ordinary day."
                )
            ),
            scene = KrishnaScene.DHYANA
        ),
        Shloka(
            id = "BG10_20",
            chapter = 10,
            verse = 20,
            speakerContext = "Sri Krishna reveals His indwelling presence",
            sanskrit = "अहमात्मा गुडाकेश सर्वभूताशयस्थितः |\nअहमादिश्च मध्यं च भूतानामन्त एव च ||१०-२०||",
            transliteration = "ahamātmā guḍākeśa sarvabhūtāśayasthitaḥ |\nahamādiśca madhyaṃ ca bhūtānāmanta eva ca ||10-20||",
            hindi = "हे गुडाकेश! मैं सब भूतों के हृदय में स्थित सबका आत्मा हूँ तथा सम्पूर्ण भूतों का आदि, मध्य और अन्त भी मैं ही हूँ।",
            english = "I am the Self, O Gudakesha, seated in the hearts of all beings. I am the beginning, the middle, and also the end of all beings.",
            explainers = listOf(
                ExplainerPerspective(
                    scholarName = "Swami Sivananda",
                    tradition = "Vedantic Purport",
                    languageLabel = "English",
                    content = "Gudakesha means 'conqueror of sleep/ignorance.' Krishna reminds Arjuna that the Divine is not distant—it shines in the very heart of every living creature."
                )
            ),
            scene = KrishnaScene.VISHWAROOPA
        ),
        Shloka(
            id = "BG13_23",
            chapter = 13,
            verse = 23,
            speakerContext = "Sri Krishna describes the silent Witness within",
            sanskrit = "उपद्रष्टानुमन्ता च भर्ता भोक्ता महेश्वरः |\nपरमात्मेति चाप्युक्तो देहेऽस्मिन्पुरुषः परः ||१३-२३||",
            transliteration = "upadraṣṭānumantā ca bhartā bhoktā maheśvaraḥ |\nparamātmeti cāpyukto dehe'sminpuruṣaḥ paraḥ ||13-23||",
            hindi = "इस देह में स्थित यह आत्मा वास्तव में परमात्मा ही है; वही साक्षी (उपद्रष्टा), अनुमति देने वाला, भरण-पोषण करने वाला, भोक्ता और महान ईश्वर कहा गया है।",
            english = "The Supreme Spirit dwelling in this body is called the Witness, the Permitter, the Supporter, the Experiencer, the Great Lord, and the Supreme Self.",
            explainers = listOf(
                ExplainerPerspective(
                    scholarName = "Swami Sivananda",
                    tradition = "Vedantic Purport",
                    languageLabel = "English",
                    content = "Beneath shifting thoughts and emotions stands the serene inner Witness (Upadrashta). Stepping back into that witness awareness brings immediate stillness."
                )
            ),
            scene = KrishnaScene.DHYANA
        ),
        Shloka(
            id = "BG14_26",
            chapter = 14,
            verse = 26,
            speakerContext = "Sri Krishna explains rising above the three Gunas",
            sanskrit = "मां च योऽव्यभिचारेण भक्तियोगेन सेवते |\nस गुणान्समतीत्यैतान्ब्रह्मभूयाय कल्पते ||१४-२६||",
            transliteration = "māṃ ca yo'vyabhicāreṇa bhaktiyogena sevate |\nsa guṇānsamatītyaitānbrahmabhūyāya kalpate ||14-26||",
            hindi = "और जो पुरुष अव्यभिचारी भक्तियोग के द्वारा मुझको निरन्तर भजता है, वह भी इन तीनों गुणों को भलीभाँति लाँघकर सच्चिदानन्दघन ब्रह्म को प्राप्त होने के योग्य हो जाता है।",
            english = "And whoever serves Me with unswerving devotion of Bhakti Yoga rises completely above the three Gunas and becomes fit for realization of the Infinite.",
            explainers = listOf(
                ExplainerPerspective(
                    scholarName = "Swami Tejomayananda",
                    tradition = "Chinmaya Mission",
                    languageLabel = "हिन्दी",
                    content = "निश्चल भक्ति के द्वारा साधक सत्व, रज और तम की सीमाओं से ऊपर उठकर परम शान्ति और ब्रह्मभाव को प्राप्त कर लेता है।"
                )
            ),
            scene = KrishnaScene.DHYANA
        ),
        Shloka(
            id = "BG15_15",
            chapter = 15,
            verse = 15,
            speakerContext = "Sri Krishna as the source of memory and wisdom",
            sanskrit = "सर्वस्य चाहं हृदि सन्निविष्टो\nमत्तः स्मृतिर्ज्ञानमपोहनं च |\nवेदैश्च सर्वैरहमेव वेद्यो\nवेदान्तकृद्वेदविदेव चाहम् ||१५-१५||",
            transliteration = "sarvasya cāhaṃ hṛdi sanniviṣṭo\nmattaḥ smṛtirjñānamapohanaṃ ca |\nvedaiśca sarvairahameva vedyo\nvedāntakṛdvedavideva cāham ||15-15||",
            hindi = "मैं ही सब प्राणियों के हृदय में अन्तर्यामी रूप से स्थित हूँ तथा मुझसे ही स्मृति, ज्ञान और उनका अपोहन (विस्मरण/संशय-निवारण) होता है। सब वेदों के द्वारा मैं ही जानने योग्य हूँ।",
            english = "I am seated in the hearts of all; from Me come memory, knowledge, and their removal. I alone am to be known through all the Vedas, and I am the author and knower of Vedanta.",
            explainers = listOf(
                ExplainerPerspective(
                    scholarName = "Swami Sivananda",
                    tradition = "Vedantic Purport",
                    languageLabel = "English",
                    content = "Intelligence, clarity, and intuition flow from the indwelling Supreme Presence. Remembering this keeps learning humble and reverent."
                )
            ),
            scene = KrishnaScene.VISHWAROOPA
        ),
        Shloka(
            id = "BG16_1",
            chapter = 16,
            verse = 1,
            speakerContext = "Sri Krishna lists noble virtues of character",
            sanskrit = "अभयं सत्त्वसंशुद्धिर्ज्ञानयोगव्यवस्थितिः |\nदानं दमश्च यज्ञश्च स्वाध्यायस्तप आर्जवम् ||१६-१||",
            transliteration = "abhayaṃ sattvasaṃśuddhirjñānayogavyavasthitiḥ |\ndānaṃ damaśca yajñaśca svādhyāyastapa ārjavam ||16-1||",
            hindi = "भय का सर्वथा अभाव, अन्तःकरण की पूर्ण निर्मलता, तत्वज्ञान के लिए ध्यानयोग में निरन्तर दृढ़ स्थिति, दान, इन्द्रिय-दमन, यज्ञ, स्वाध्याय, तप और सरलता — ये दैवी सम्पदा के लक्षण हैं।",
            english = "Fearlessness, purity of heart, steadfastness in knowledge and yoga, charity, self-restraint, sacred offering, self-study, discipline, and straightforwardness—these are the divine qualities.",
            explainers = listOf(
                ExplainerPerspective(
                    scholarName = "Swami Sivananda",
                    tradition = "Vedantic Purport",
                    languageLabel = "English",
                    content = "Notice that the very first virtue Krishna names is Abhayam—fearlessness born of inner purity and trust in truth."
                )
            ),
            scene = KrishnaScene.KARMA
        ),
        Shloka(
            id = "BG17_3",
            chapter = 17,
            verse = 3,
            speakerContext = "Sri Krishna explains the shaping power of faith",
            sanskrit = "सत्त्वानुरूपा सर्वस्य श्रद्धा भवति भारत |\nश्रद्धामयोऽयं पुरुषो यो यच्छ्रद्धः स एव सः ||१७-३||",
            transliteration = "sattvānurūpā sarvasya śraddhā bhavati bhārata |\nśraddhāmayo'yaṃ puruṣo yo yacchraddhaḥ sa eva saḥ ||17-3||",
            hindi = "हे भारत! सभी मनुष्यों की श्रद्धा उनके अन्तःकरण के अनुरूप होती है। यह पुरुष श्रद्धामय है, इसलिए जो पुरुष जैसी श्रद्धा वाला होता है, वह स्वयं भी वही (उसका स्वरूप) होता है।",
            english = "The faith of every person conforms to their inner nature, O Bharata. A human being is made of faith; whatever one's deep faith is, that verily one becomes.",
            explainers = listOf(
                ExplainerPerspective(
                    scholarName = "Swami Chinmayananda",
                    tradition = "The Holy Geeta",
                    languageLabel = "English",
                    content = "Our deepest convictions quietly sculpt our character. Guard what you value and revere, because you gradually grow into the likeness of what you hold sacred."
                )
            ),
            scene = KrishnaScene.KARMA
        ),
        Shloka(
            id = "BG18_78",
            chapter = 18,
            verse = 78,
            speakerContext = "Sanjaya concludes the Bhagavad Gita",
            sanskrit = "यत्र योगेश्वरः कृष्णो यत्र पार्थो धनुर्धरः |\nतत्र श्रीर्विजयो भूतिर्ध्रुवा नीतिर्मतिर्मम ||१८-७८||",
            transliteration = "yatra yogeśvaraḥ kṛṣṇo yatra pārtho dhanurdharaḥ |\ntatra śrīrvijayo bhūtirbhruvā nītirmatirmama ||18-78||",
            hindi = "जहाँ योगेश्वर भगवान श्रीकृष्ण हैं और जहाँ गाण्डीव धनुषधारी अर्जुन हैं, वहीं श्री, विजय, विभूति और अचल नीति है — ऐसा मेरा दृढ़ मत है।",
            english = "Wherever there is Krishna, the Lord of Yoga, and wherever there is Arjuna, the dedicated archer of action, there surely abide prosperity, victory, flourishing, and unshakeable righteousness.",
            explainers = listOf(
                ExplainerPerspective(
                    scholarName = "Swami Chinmayananda",
                    tradition = "The Holy Geeta",
                    languageLabel = "English",
                    content = "The Gita closes with a timeless formula for fulfillment: Krishna represents Higher Wisdom, and Arjuna represents Sincere Effort. Where wisdom guides earnest action, true success naturally follows."
                )
            ),
            scene = KrishnaScene.KURUKSHETRA
        )
    ).map { it.withRanking() }.associateBy { it.id }
}
