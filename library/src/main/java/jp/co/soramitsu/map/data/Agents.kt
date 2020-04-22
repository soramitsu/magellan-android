package jp.co.soramitsu.map.data

import jp.co.soramitsu.map.model.Category
import jp.co.soramitsu.map.model.Place
import jp.co.soramitsu.map.model.Position

/**
 * All agents on the map. Agent is an authorized person who can help user with
 * withdraw and top up his account
 */
internal val Places.agents
    get() = listOf(
        Place(
            name = "Nguon Sitha",
            category = Category.BANK,
            position = Position(11.5525763, 104.910986),
            address = "Phnom Penh. Chamkar Mon. Tuol Svay Prey Ti Muoy"
        ),
        Place(
            name = "Rida. Kouch Sopanha ",
            category = Category.BANK,
            position = Position(11.5535017, 104.9133075),
            address = "Phnom Penh. Chamkar Mon. Olympic"
        ),
        Place(
            name = "Nget Chan",
            category = Category.BANK,
            position = Position(11.5486218, 104.930498),
            address = "Phnom Penh. Chamkar Mon. Tonle Basak"
        ),
        Place(
            name = "Thear Sombo. Cheng Soksambo ",
            category = Category.BANK,
            position = Position(11.5342194, 104.9192572),
            address = "Phnom Penh. Chamkar Mon. Phsar Daeum Thkov"
        ),
        Place(
            name = "432 Express. Chhan Sreypov ",
            category = Category.BANK,
            position = Position(11.5418557, 104.9216885),
            address = "Phnom Penh. Chamkar Mon. Boeng Trabaek"
        ),
        Place(
            name = "Ear Chheang Hong. Doung Sokhom ",
            category = Category.BANK,
            position = Position(11.5447665, 104.9005492),
            address = "Phnom Penh. Chamkar Mon. Tumnob Tuek"
        ),
        Place(
            name = "Chiv Ra",
            category = Category.BANK,
            position = Position(11.5535211, 104.9108823),
            address = "Phnom Penh. Chamkar Mon. Olympic"
        ),
        Place(
            name = "Visal. Kchao Visal ",
            category = Category.BANK,
            position = Position(11.5512493, 104.9161006),
            address = "Phnom Penh. Chamkar Mon. Olympic"
        ),
        Place(
            name = "Pheng Leap. Ou Sreyleap ",
            category = Category.BANK,
            position = Position(11.5326488, 104.9142098),
            address = "Phnom Penh. Chamkar Mon. Phsar Daeum Thkov"
        ),
        Place(
            name = "Phanna. Chan Raksmey ",
            category = Category.BANK,
            position = Position(11.5247547, 104.9406874),
            address = "Phnom Penh. Chbar Ampov. Preaek Pra"
        ),
        Place(
            name = "Ly Pheng. Mean Mouyhouy ",
            category = Category.BANK,
            position = Position(11.5319555, 104.9356896),
            address = "Phnom Penh. Chbar Ampov. Chhbar Ampov Ti Muoy"
        ),
        Place(
            name = "Mei Ling. Hour Sreyneang ",
            category = Category.BANK,
            position = Position(11.53297959, 104.99124479),
            address = "Phnom Penh. Chbar Ampov. Preaek Aeng"
        ),
        Place(
            name = "Psa Kaksiphal. Pen Chanborin ",
            category = Category.BANK,
            position = Position(11.5885, 104.936),
            address = "Phnom Penh. Chraoy Chongvar. Chrouy Changvar"
        ),
        Place(
            name = "Vithey Café. Sorn Saveth ",
            category = Category.BANK,
            position = Position(11.6417817, 104.917715),
            address = "Phnom Penh. Chraoy Chongvar. Preaek Lieb"
        ),
        Place(
            name = "Sum Vanna II. Sum Vanna ",
            category = Category.BANK,
            position = Position(11.64486, 104.9167917),
            address = "Phnom Penh. Chraoy Chongvar. Preaek Lieb"
        ),
        Place(
            name = "Ngauv Heng 168. Ngauv Bunhieng ",
            category = Category.BANK,
            position = Position(11.5725043, 104.9244954),
            address = "Phnom Penh. Doun Penh. Voat Phnum"
        ),
        Place(
            name = "Wing Wei Luy. Ren Ny ",
            category = Category.BANK,
            position = Position(11.568829, 104.9210228),
            address = "Phnom Penh. Doun Penh. Phsar Kandal Ti Muoy"
        ),
        Place(
            name = "Hean Lina. Lor Soley ",
            category = Category.BANK,
            position = Position(11.56574093, 104.92788658),
            address = "Phnom Penh. Doun Penh. Chey Chumneah"
        ),
        Place(
            name = "Royal RailWay . Royal RailWay Co., LTD. ",
            category = Category.BANK,
            position = Position(11.57257348, 104.91694543),
            address = "Phnom Penh. Doun Penh. Srah Chak"
        ),
        Place(
            name = "Vireak BunTham. Bun Chanthou ",
            category = Category.BANK,
            position = Position(11.58017725, 104.92017774),
            address = "Phnom Penh. Doun Penh. Voat Phnum"
        ),
        Place(
            name = "Thavra. Sing Sowatero ",
            category = Category.BANK,
            position = Position(11.54187459, 104.90182875),
            address = "Phnom Penh. Mean Chey. Stueng Mean Chey"
        ),
        Place(
            name = "Sum Sokunthea",
            category = Category.BANK,
            position = Position(11.54348748, 104.87935255),
            address = "Phnom Penh. Mean Chey. Stueng Mean Chey"
        ),
        Place(
            name = "Visal. Sorn Chantha ",
            category = Category.BANK,
            position = Position(11.5411243, 104.8950792),
            address = "Phnom Penh. Mean Chey. Stueng Mean Chey 1"
        ),
        Place(
            name = "Ngoun Channavy",
            category = Category.BANK,
            position = Position(11.5337328, 104.908998),
            address = "Phnom Penh. Mean Chey. Boeng Tumpun 1"
        ),
        Place(
            name = "Sambo Dariza. Say Sambo ",
            category = Category.BANK,
            position = Position(11.4991211, 104.94137214),
            address = "Phnom Penh. Mean Chey. Chak Angrae Kraom"
        ),
        Place(
            name = "Sophy Saren. Sang Phy ",
            category = Category.BANK,
            position = Position(11.5240361, 104.8953463),
            address = "Phnom Penh. Mean Chey. Stueng Mean Chey 2"
        ),
        Place(
            name = "Chay Sotheary",
            category = Category.BANK,
            position = Position(11.52443642, 104.89381431),
            address = "Phnom Penh. Mean Chey. Stueng Mean Chey"
        ),
        Place(
            name = "Seng Dakhourch",
            category = Category.BANK,
            position = Position(11.6600595, 104.8591287),
            address = "Phnom Penh. Praek Pnov. Preaek Phnov"
        ),
        Place(
            name = "Solyna. Yeun Sokthy ",
            category = Category.BANK,
            position = Position(11.68234, 104.8454533),
            address = "Phnom Penh. Praek Pnov. Preaek Phnov"
        ),
        Place(
            name = "Suy Inlady. Ty Somaly ",
            category = Category.BANK,
            position = Position(11.6553406, 104.8640066),
            address = "Phnom Penh. Praek Pnov. Preaek Phnov"
        ),
        Place(
            name = "Kan Malai",
            category = Category.BANK,
            position = Position(11.52840408, 104.83962859),
            address = "Phnom Penh. Pur SenChey. Chaom Chau 2"
        ),
        Place(
            name = "Pov Tola. Sim Tola ",
            category = Category.BANK,
            position = Position(11.5271836, 104.8526175),
            address = "Phnom Penh. Pur SenChey. Chaom Chau 1"
        ),
        Place(
            name = "Neak Seiha",
            category = Category.BANK,
            position = Position(11.5278022, 104.8244162),
            address = "Phnom Penh. Pur SenChey. Chaom Chau 3"
        ),
        Place(
            name = "Korm Pavchin",
            category = Category.BANK,
            position = Position(11.5473908, 104.8289555),
            address = "Phnom Penh. Pur SenChey. Chaom Chau 3"
        ),
        Place(
            name = "Thai Hour. Chiv Rattana ",
            category = Category.BANK,
            position = Position(11.56687295, 104.8209692),
            address = "Phnom Penh. Pur SenChey. Samraong Kraom (PP)"
        ),
        Place(
            name = "Prochea Prey. Pheav Sokunthea ",
            category = Category.BANK,
            position = Position(11.5863924, 104.9069808),
            address = "Phnom Penh. Russey Keo. Tuol Sangkae 1"
        ),
        Place(
            name = "Dao Roka",
            category = Category.BANK,
            position = Position(11.61558, 104.911136),
            address = "Phnom Penh. Russey Keo. Kiloumaetr Lekh Prammuoy"
        ), Place(
            name = "Reaksmey. Morb Kosomreaksmey ",
            category = Category.BANK,
            position = Position(11.5733, 104.876),
            address = "Phnom Penh. Saensokh. Phnom Penh Thmei"
        ),
        Place(
            name = "Ing Bunchay",
            category = Category.BANK,
            position = Position(11.5567352, 104.88638056),
            address = "Phnom Penh. Saensokh. Tuek Thla (PP)"
        ),
        Place(
            name = "El La (Hanoi). Rin Chanrotha ", category = Category.BANK,
            position = Position(11.5778655, 104.870184),
            address = "Phnom Penh. Saensokh. Phnom Penh Thmei"
        ),
        Place(
            name = "Chamroeun. Yan Chamroeun ",
            category = Category.BANK, position = Position(11.5547703, 104.9022606),
            address = "Phnom Penh. Tuol Kouk. Phsar Daeum Kor"
        ),
        Place(
            name = "Ung Kimsry",
            category = Category.BANK,
            position = Position(11.5644001, 104.9001267),
            address = "Phnom Penh. Tuol Kouk. Tuek L'ak Ti Pir"
        ),
        Place(
            name = "Sansam. Sun Dymo ",
            category = Category.BANK,
            position = Position(11.567099, 104.906115),
            address = "Phnom Penh. Tuol Kouk. Phsar Depou Ti Muoy"
        ),
        Place(
            name = "Trok Dara",
            category = Category.BANK,
            position = Position(11.57746067, 104.89071074),
            address = "Phnom Penh. Tuol Kouk. Boeng Kak Ti Pir"
        ), Place(
            name = "Hay Vannak. Dean Hengsreyleak ",
            category = Category.BANK,
            position = Position(11.44699808, 104.80479311),
            address = "Phnom Penh. Dangkao. Krang Pongro"
        ), Place(
            name = "Ly Heng. Kun Kanika ",
            category = Category.BANK,
            position = Position(11.5225658333333, 104.888381944444),
            address = "Phnom Penh. Dangkao. Dangkao"
        ),
        Place(
            name = "Huot Hongrit",
            category = Category.BANK,
            position = Position(11.56490561, 104.91835482),
            address = "Phnom Penh. Prampir Meakkakra (PP). Ou Ruessei Ti Buon"
        ),
        Place(
            name = "Song Si Chan. Chhan Chan ", category = Category.BANK,
            position = Position(11.56640163, 104.9164455),
            address = "Phnom Penh. Prampir Meakkakra (PP). Ou Ruessei Ti Pir"
        ),
        Place(
            name = "Prochea Prey. Pheav Sokunthea ",
            category = Category.BANK, position = Position(11.5863924, 104.9069808),
            address = "Phnom Penh. Russey Keo. Tuol Sangkae 1"
        ),
        Place(
            name = "Samroung Orndet. Im Mardy ",
            category = Category.BANK,
            position = Position(11.5849371, 104.8658917),
            address = "Phnom Penh. Saensokh. Kouk Khleang"
        )
    )