package jp.co.soramitsu.feature.maps.impl.data

import jp.co.soramitsu.feature.maps.impl.model.Category
import jp.co.soramitsu.feature.maps.impl.model.Place
import jp.co.soramitsu.feature.maps.impl.model.Position

/**
 * All shops, supermarkets, stores and so on that will be displayed on the map
 */
val Places.merchants
    get() = listOf(
        // region merchants
        Place(
            name = "Botra Shop",
            category = Category.SUPERMARKETS,
            position = Position(11.512413, 104.883919),
            phone = "070277366",
            address = "St. 07, Borey Pipop Thmey Chamkardong, Phom Penh"
        ),
        Place(
            name = "SK Coffee & Sandwich ",
            category = Category.FOOD,
            position = Position(11.5312340, 104.9249727),
            phone = "070283238",
            address = "St.508,Sangkat.Beok Trobek,Khan Chamkamom, Phom Penh"
        ),
        Place(
            name = "Chenda Beauty Shop",
            category = Category.SUPERMARKETS,
            position = Position(11.545633, 104.833476),
            phone = "098808689",
            address = "Phum. Preytea,Sangkat.Chom Chao,Khan.Pou senchey,Phnom Penh"
        ),
        Place(
            name = "Tola Phone shop",
            category = Category.SUPERMARKETS,
            position = Position(11.502584, 104.939886),
            phone = "0978688886",
            address = "Address: 1645B, Sangkat.Chak Angre Krom,Khan. Mean Chey, Phnom Penh"
        ),
        Place(
            name = "Bro & Sis Cooktail",
            category = Category.FOOD,
            position = Position(11.553555, 104.932452),
            phone = "015547456",
            address = "St.Sothearos"
        ),
        Place(
            name = "Chenla Smart Phone ",
            category = Category.SUPERMARKETS,
            position = Position(11.551733, 104.901915),
            phone = "098848135",
            address = "#109Eo,St.217,(Elbow.Intercon) Phnom Penh."
        ),
        Place(
            name = "Mr.Teng 168 Phone Shop",
            category = Category.SUPERMARKETS,
            position = Position(11.541524, 104.913585),
            phone = "098848135",
            address = "Near Phsar Toul TumPung,#84,St.432"
        ),
        Place(
            name = "President Phone Shop ",
            category = Category.SUPERMARKETS,
            position = Position(11.5660, 104.902112),
            phone = "098848135",
            address = "Kampuchea Krom Blvd(128) Phnom Penh"
        ),
        Place(
            name = "Coffee Shop",
            category = Category.FOOD,
            position = Position(11.530512, 104.925992),
            phone = "068477770",
            address = "Near Sathapana Chamkamon Branch "
        ),
        Place(
            name = "Leang Muykorng",
            category = Category.SUPERMARKETS,
            position = Position(11.531305, 104.925161),
            phone = "081394710",
            address = "St.Prekmonivung,Chamkamon, Phnom Penh"
        ),
        Place(
            name = "Up door Fashion",
            category = Category.SUPERMARKETS,
            position = Position(11.548894, 104.916019),
            phone = "016222146",
            address = "St.Prekmonivung,Chamkamon, Phnom Penh"
        ),
        Place(
            name = "Rominea Restaurant",
            category = Category.FOOD,
            position = Position(11.530368, 104.926036),
            phone = "012631996",
            address = "St  143 , Boeung Keng korng 3, Phnom Penh"
        ),
        Place(
            name = "Angko Khmer Pranit (KP Rice) ",
            category = Category.SUPERMARKETS,
            position = Position(11.601922, 104.910346),
            phone = "098888884",
            address = "St.508,Sangkat.Beok Trobek,Khan Chamkamom, Phom Penh"
        ),
        Place(
            name = "Aryne Phone Shop",
            category = Category.SUPERMARKETS,
            position = Position(11.568577, 104.906440),
            phone = "089585656",
            address = "St.508, Phsar Derm, Chamkamon, Phnom Penh "
        ),
        Place(
            name = "Touch Sreylank Shop",
            category = Category.SUPERMARKETS,
            position = Position(11.543830, 104.832328),
            phone = "011921611",
            address = "#08, St. 907,Russey Keo Phnom Penh"
        ),
        Place(
            name = "Bun Sroy Shop",
            category = Category.SUPERMARKETS,
            position = Position(11.578787, 104.932365),
            phone = "099777795",
            address = "Depo III, Toulkok, Phnom Penh"
        ),
        Place(
            name = "Boeng Trobeak Phone Shop",
            category = Category.SUPERMARKETS,
            position = Position(11.533094, 104.924038),
            phone = "0964937036",
            address = "#114 PREY TEA 01 CHAOM CHAU 03 PUR SENCHEY, PHNOM PENH"
        ),
        Place(
            name = "Eung Sophanna",
            category = Category.SUPERMARKETS,
            position = Position(11.531187, 104.924668),
            phone = "017445444",
            address = "Phsar Km6 Market Russey Keo Phom Penh"
        ),
        Place(
            name = "Lor Penh chet Printing ",
            category = Category.OTHER,
            position = Position(11.566988, 104.898697),
            phone = "012508088",
            address = "#813E ,St Preah monivong, Phnom Penh "
        ),
        Place(
            name = "Kes Sophek",
            category = Category.FOOD,
            position = Position(11.526518, 104.962039),
            phone = "012878479",
            address = "Toul Tum Pung Market"
        ),
        Place(
            name = "Khim Ratana",
            category = Category.SUPERMARKETS,
            position = Position(11.480407, 104.945420),
            phone = "011625325",
            address = "Near Hengly Branch "
        ),
        Place(
            name = "Yan Sreypich",
            category = Category.FOOD,
            position = Position(11.531607, 104.924792),
            phone = "081570009",
            address = "#69,St.P16,Borey Peng Hout Boeung Snor,Phnom Penh"
        ),
        Place(
            name = "Tep Vichivorn",
            category = Category.SUPERMARKETS,
            position = Position(11.526781, 104.920788),
            phone = "012658067",
            address = "#625, St 128, Kampuchea Krom Blvd, Cambodia, Phnom Penh"
        ),
        Place(
            name = "Cheourng Rann",
            category = Category.SUPERMARKETS,
            position = Position(11.531310, 104.925174),
            phone = "081231111",
            address = "St.21, Takhmao Kandal"
        ),
        Place(
            name = "Ou Lyna",
            category = Category.SUPERMARKETS,
            position = Position(11.540735, 104.914708),
            phone = "017777785",
            address = "St.271,Phsar Demkor, Chamkamon, Phnom Penh"
        ),
        Place(
            name = "Kong Thavy",
            category = Category.SUPERMARKETS,
            position = Position(11.565596, 104.921526),
            phone = "011829266",
            address = "St.Prekmonivung,Chamkamon, Phnom Penh"
        ),
        Place(
            name = "Siek Tepy",
            category = Category.SUPERMARKETS,
            position = Position(11.577904, 104.917321),
            phone = "",
            address = "Preah Monivong Blvd., Sangkat Srah Chork, Khan Daun Penh, Phnom Penh, Cambodia."
        ),
        Place(
            name = "Long Chhainav",
            category = Category.SUPERMARKETS,
            position = Position(11.569055, 104.922522),
            phone = "",
            address = "136 Samdech Preah Sangk Neayok Souk St. (136)"
        ),
        Place(
            name = "Hou Hokpheng",
            category = Category.FOOD,
            position = Position(11.5416074, 104.9163430),
            phone = "",
            address = "#60c, St.432, Toul Tompoun g"
        ),
        Place(
            name = "Chheng Cchunsreng",
            category = Category.SUPERMARKETS,
            position = Position(11.5407816, 104.9152493),
            phone = "011315156",
            address = "#31, St. 155, Toul Tompoung Market"
        ),
        Place(
            name = "Lim Morin",
            category = Category.FOOD,
            position = Position(11.534563, 104.922848),
            phone = "093598787",
            address = "St. 484, Boeung Trobek"
        ),
        Place(
            name = "Phan Mesa",
            category = Category.SUPERMARKETS,
            position = Position(11.575609, 104.853554),
            phone = "087373894",
            address = "Boeung Chhouk Market"
        ),
        Place(
            name = "Heng Vongrady",
            category = Category.OTHER,
            position = Position(11.557066, 104.895089),
            phone = "012955838",
            address = "#57D,St230 , Toek lork, PP"
        ),
        Place(
            name = "Heng Vongrady",
            category = Category.PHARMACY,
            position = Position(11.510809, 104.936713),
            phone = "092119011",
            address = "#42, N 2 Chak Angre Leu , PP"
        ),
        Place(
            name = "Im Malene",
            category = Category.SUPERMARKETS,
            position = Position(11.541669, 104.917826),
            phone = "092119011",
            address = "#32,St432,Toul Tompong1 Phnom Penh"
        ),
        Place(
            name = "Chamnab David",
            category = Category.SUPERMARKETS,
            position = Position(11.546722, 104.908499),
            phone = "010222236",
            address = "#136C,St Mao Sengtong,Chomkamorn PP"
        ),
        Place(
            name = "Mach Huot",
            category = Category.SUPERMARKETS,
            position = Position(11.543830, 104.832328),
            phone = "098234984",
            address = "Phum. Preytea,Sangkat.Chom Chao,Khan.Pou senchey,Phnom Penh"
        ),
        Place(
            name = "Prum Samoeun ",
            category = Category.SUPERMARKETS,
            position = Position(11.543830, 104.832328),
            phone = "085835818",
            address = "Phum. Preytea,Sangkat.Chom Chao,Khan.Pou senchey,Phnom Penh"
        ),
        Place(
            name = "Bo Vanlyna",
            category = Category.SUPERMARKETS,
            position = Position(11.543830, 104.832328),
            phone = "017480694",
            address = "Phum. Preytea,Sangkat.Chom Chao,Khan.Pou senchey,Phnom Penh"
        ),
        Place(
            name = "Kong Sovanarath",
            category = Category.SUPERMARKETS,
            position = Position(11.553456, 104.886448),
            phone = "0962778393",
            address = "Near Hengly Branch "
        ),
        Place(
            name = "Yon Vuthy",
            category = Category.SUPERMARKETS,
            position = Position(11.540640, 104.914471),
            phone = "012634462",
            address = "Toul Tum Pung Market"
        ),
        Place(
            name = "Som Setha",
            category = Category.FOOD,
            position = Position(11.534499, 104.919116),
            phone = "012728686",
            address = "Psar Dermtkov"
        ),
        Place(
            name = "Seang Lybunrong",
            category = Category.FOOD,
            position = Position(11.533121, 104.915555),
            phone = "095761661",
            address = "Psar Dermtkov"
        ),
        Place(
            name = "HaK Kunthea",
            category = Category.PHARMACY,
            position = Position(11.531368, 104.925018),
            phone = "012634462",
            address = "Boeung Trobek"
        ),
        Place(
            name = "Kim Sopheap",
            category = Category.FOOD,
            position = Position(11.5503259, 104.9060437),
            phone = "070825258",
            address = "Toul Svay Prey"
        ),
        Place(
            name = "Nhoem Touch",
            category = Category.FOOD,
            position = Position(11.530955, 104.925559),
            phone = "966043323",
            address = "Boeung Trobek"
        ),
        Place(
            name = "Khy Hong Veng",
            category = Category.SUPERMARKETS,
            position = Position(11.551010, 104.906950),
            phone = "016888771",
            address = "Toul Svay Prey"
        ),
        Place(
            name = "Khy Hong Veng",
            category = Category.SUPERMARKETS,
            position = Position(11.567189, 104.922519),
            phone = "016888771",
            address = "Soriya market"
        ),
        Place(
            name = "Khy Hong Veng",
            category = Category.SUPERMARKETS,
            position = Position(11.561564, 104.872301),
            phone = "016888771",
            address = "Ratana Plaza"
        ),
        Place(
            name = "Khy Hong Veng",
            category = Category.SUPERMARKETS,
            position = Position(11.541197, 104.919511),
            phone = "016888771",
            address = "Boeung Trobek"
        ),
        Place(
            name = "Khy Hong Veng",
            category = Category.SUPERMARKETS,
            position = Position(11.558466, 104.909419),
            phone = "016888771",
            address = "City Mall"
        ),
        Place(
            name = "Khy Hong Veng",
            category = Category.SUPERMARKETS,
            position = Position(11.575835, 104.896475),
            phone = "016888771",
            address = "Toul Kok"
        ),
        Place(
            name = "THON PHALLA",
            category = Category.OTHER,
            position = Position(11.548505, 104.904827),
            phone = "012 528 438",
            address = "ផ្ទះលេខ133, ផ្លូវ199, ភូមិ១,ទំនប់ទឹក, ចំការមន, ភ្នំពេញ"
        ),
        Place(
            name = "KAY KIMLIN",
            category = Category.OTHER,
            position = Position(11.552187, 104.913762),
            phone = "011 636337",
            address = "ផ្ទះលេខ6E, ផ្លូវ163, ភូមិ ៥, សង្កាត់អូឡាំពិក, ខណ្ឌចំការមន, រាជធានីភ្នំពេញ"
        ),
        Place(
            name = " EAR SIVGECH",
            category = Category.OTHER,
            position = Position(11.562190, 104.871821),
            phone = "070 496990",
            address = "ផ្ទះF62, ផ្លូវហ្វ័រជន, ចុងថ្នល់ខាងកើត, សង្កាត់ទឹកថ្លា, ខណ្ឌសែនសុខ, រាជធានីភ្នំពេញ"
        ),
        Place(
            name = "Sreang Chan Vanraksmey ",
            category = Category.OTHER,
            position = Position(11.5729495, 104.8727791),
            phone = "088 355 5559",
            address = "ផ្ទះC2,ផ្លូវ១៩៨៦,ភ្នំពេញថ្មី, ភ្នំពេញថ្មី, សែនសុខ, ភ្នំពេញ"
        ),
        Place(
            name = "THIEM ONN",
            category = Category.OTHER,
            position = Position(11.5955310, 104.9156250),
            phone = "092 666 998",
            address = "ផ្លូវ93,ផ្ទះA6,ចុងខ្សាច់,ទូលសង្កែ,ឬស្សីកែវ,ភ្នំពេញ"
        ),
        Place(
            name = "LIM HOUY",
            category = Category.OTHER,
            position = Position(11.6120646, 104.9165816),
            phone = "016 314382",
            address = "ផ្ទះ924, ផ្លូវ5, មិត្តភាព, ឬស្សីកែវ, ឬស្សីកែវ, ភ្នំពេញ "
        ),
        Place(
            name = "Chou Thida ",
            category = Category.OTHER,
            position = Position(11.6221298, 104.8463833),
            phone = "012 609 921",
            address = "ផ្ទះ06CP, ផ្លូវ232, សែនសុខទី៣, សង្កាត់ឃ្មួញ, ខណ្ឌសែនសុខ, រាជធានីភ្នំពេញ"
        ),
        Place(
            name = "DECAFF COFFEE",
            category = Category.FOOD,
            position = Position(11.562312, 104.925896),
            phone = "012 488 892",
            address = "ST. 208, KHAN DAUN PENH, PHNOMPENH (NEAR VATTANAC BANK NORODOM BRANCH)"
        ),
        Place(
            name = "PHEAKTRA GROCERY STORE",
            category = Category.SUPERMARKETS,
            position = Position(11.563648, 104.886925),
            phone = "017 999 558",
            address = "# 54F, St. 138, Phum Tropang Chhouk, Sangkat Teak Thlar, Khan Sen Sok, Phnom Penh"
        ),
        Place(
            name = "DOEUM MIEN RICE SHOP",
            category = Category.FOOD,
            position = Position(11.562201, 104.925761),
            phone = "077 646 802",
            address = "# 3, ST 208, SANGKAT BOEUNG RANG, KHAN DAUN PENH, PHNOM PENH (Infront of Decaf Coffee) "
        ),
        Place(
            name = "WATHAN ARTISANS CAMBODIA",
            category = Category.SUPERMARKETS,
            position = Position(11.560265, 104.927963),
            phone = "087 317 979",
            address = "No. 77, Street 240, Sangkat Chey Chomneas, Khan Daun Penh, Phnom Penh."
        ),
        Place(
            name = "THE KS BAR",
            category = Category.ENTERTAINMENT,
            position = Position(11.562347, 104.926080),
            phone = "061 895 533",
            address = "1st Floor, #8 ST. 208 (ABOVE THE VITO), KHAN DAUN PENH, PHNOM PENH"
        ),
        Place(
            name = "D S COFFEE",
            category = Category.FOOD,
            position = Position(11.540344, 104.915413),
            phone = "015 987 765",
            address = "East of Toul Tumpong Market, Street 155, Sangkat Toul Tumpong I, Khan Chamkarkorm"
        ),
        Place(
            name = "CMART DIRECT ",
            category = Category.SUPERMARKETS,
            position = Position(11.561941, 104.890333),
            phone = "096 666 1155",
            address = "# 334, st 271 terk laok III, khan toul kork, Phnom Penh"
        ),
        Place(
            name = "MI HOME",
            category = Category.SUPERMARKETS,
            position = Position(11.571231, 104.897284),
            phone = "087 805 678",
            address = "# 687CD, Kampuchea Krom, Teouk Laok I, Khan Toul Kork, Phnom Penh"
        ),
        Place(
            name = "TECHZONE",
            category = Category.SUPERMARKETS,
            position = Position(11.556658, 104.917157),
            phone = "077 668 080",
            address = "# 228Eo, Preah Sihanouk blvd (st274) Phnom Penh"
        ),
        Place(
            name = "LE BOOST CAFÉ",
            category = Category.FOOD,
            position = Position(11.573463, 104.919622),
            phone = "077 366 336",
            address = "# 21F, Stree 67, Behind Vattanac Tower, Phnom Penh, Cambodia"
        ),
        Place(
            name = "Lucky Burger",
            category = Category.FOOD,
            position = Position(11.574479, 104.921022),
            phone = "099 222 481",
            address = "Exchange Square Building"
        ),
        Place(
            name = "Xin Tian Di vegetarian  food",
            category = Category.FOOD,
            position = Position(11.562115, 104.924549),
            phone = "069 296 299",
            address = "No.100, St.51 Duan Penh Phnom Penh"
        ),
        Place(
            name = "%Arabica_Shop",
            category = Category.FOOD,
            position = Position(11.574116, 104.918407),
            phone = "086 680 801",
            address = "Vattanac Capital, Ground Floor,No.66, Preah Monivong Blvd,Sangkat Wat Phnom, Khan Daun Penh,Phnom Penh,Cambodia"
        ),
        Place(
            name = "Café Malaya",
            category = Category.SUPERMARKETS,
            position = Position(11.571389, 104.920338),
            phone = "0976830289",
            address = "#65E0, Street 118, Sangkat Phsar Thmey I, Khan Duan Penh, Phnom Penh, Cambodia."
        ),
        Place(
            name = "Coffee Shop",
            category = Category.FOOD,
            position = Position(11.558883, 104.925193),
            phone = "",
            address = ""
        ),
        Place(
            name = "Frank Coffee",
            category = Category.FOOD,
            position = Position(11.550069, 104.9309675),
            phone = "012449889",
            address = "AEON Area"
        ),
        Place(
            name = "Start Chas luk Kuyteav Bay",
            category = Category.FOOD,
            position = Position(11.5467195, 104.9210625),
            phone = "011890699",
            address = "Behind Wing Office"
        ),
        Place(
            name = "Jak Kjao",
            category = Category.FOOD,
            position = Position(11.5447509, 104.9214993),
            phone = "098434444",
            address = "Behind Wing Office"
        ),
        Place(
            name = "Kawaii",
            category = Category.SUPERMARKETS,
            position = Position(11.5464283, 104.921078),
            phone = "098881199",
            address = "Behind Wing Office"
        ),
        Place(
            name = "Flower Factory",
            category = Category.SUPERMARKETS,
            position = Position(11.5508667, 104.918852),
            phone = "010499992",
            address = "Tuol Svay Prey"
        ),
        Place(
            name = "QC Chhay",
            category = Category.FOOD,
            position = Position(11.5556251, 104.9117434),
            phone = "089644366",
            address = "Olympic"
        ),
        Place(
            name = "Rich Reay",
            category = Category.SUPERMARKETS,
            position = Position(11.5681137, 104.9209853),
            phone = "011337738",
            address = "Central Market"
        ),
        Place(
            name = "R Caffee",
            category = Category.FOOD,
            position = Position(11.5454917, 104.9213849),
            phone = "011817272",
            address = "Behind Wing Office"
        ),
        Place(
            name = "Lian Xin",
            category = Category.SUPERMARKETS,
            position = Position(11.5683371, 104.9211857),
            phone = "092338998",
            address = "Central Market"
        ),
        Place(
            name = "Five Stars Optics and Watch Shop",
            category = Category.SUPERMARKETS,
            position = Position(11.5675056, 104.9213374),
            phone = "099752527",
            address = "Central Market"
        ),
        Place(
            name = "Marry Phone Shop",
            category = Category.SUPERMARKETS,
            position = Position(11.5702882, 104.9212442),
            phone = "0967333777",
            address = "Central Market"
        ),
        Place(
            name = "Chiv Por",
            category = Category.SUPERMARKETS,
            position = Position(11.5680791, 104.9207183),
            phone = "085558888",
            address = "Central Market"
        ),
        Place(
            name = "Amom",
            category = Category.SUPERMARKETS,
            position = Position(11.5697258, 104.9210242),
            phone = "0967700227",
            address = "Central Market"
        ),
        Place(
            name = "I1013",
            category = Category.SUPERMARKETS,
            position = Position(11.5697258, 104.9210242),
            phone = "087988686",
            address = "Central Market"
        ),
        Place(
            name = "I1002",
            category = Category.SUPERMARKETS,
            position = Position(11.5697258, 104.9210242),
            phone = "093250116",
            address = "Central Market"
        ),
        Place(
            name = "I4053",
            category = Category.SUPERMARKETS,
            position = Position(11.5697258, 104.9210242),
            phone = "086600323",
            address = "Central Market"
        ),
        Place(
            name = "E340",
            category = Category.SUPERMARKETS,
            position = Position(11.5697258, 104.9210242),
            phone = "012719241",
            address = "Central Market"
        ),
        Place(
            name = "E212",
            category = Category.SUPERMARKETS,
            position = Position(11.5697258, 104.9210242),
            phone = "017845984",
            address = "Central Market"
        ),
        Place(
            name = "Join Café",
            category = Category.FOOD,
            position = Position(11.5259986, 104.8836513),
            phone = "089325033",
            address = "Steung Meanchey"
        ),
        Place(
            name = "Mrs Coffee",
            category = Category.FOOD,
            position = Position(11.5526682, 104.8897399),
            phone = "010666159",
            address = "Boeng Salang"
        ),
        Place(
            name = "I1005",
            category = Category.SUPERMARKETS,
            position = Position(11.5697258, 104.9210242),
            phone = "012975436",
            address = "Central Market"
        ),
        Place(
            name = "I3060",
            category = Category.SUPERMARKETS,
            position = Position(11.5697258, 104.9210242),
            phone = "098909698",
            address = "Central Market"
        ),
        Place(
            name = "E338",
            category = Category.SUPERMARKETS,
            position = Position(11.5697258, 104.9210242),
            phone = "098868262",
            address = "Central Market"
        ),
        Place(
            name = "I3048",
            category = Category.SUPERMARKETS,
            position = Position(11.5697258, 104.9210242),
            phone = "069436631",
            address = "Central Market"
        ),
        Place(
            name = "E179",
            category = Category.SUPERMARKETS,
            position = Position(11.5697258, 104.9210242),
            phone = "092202946",
            address = "Central Market"
        ),
        Place(
            name = "E182",
            category = Category.SUPERMARKETS,
            position = Position(11.5697258, 104.9210242),
            phone = "011211103",
            address = "Central Market"
        ),
        Place(
            name = "E068",
            category = Category.SUPERMARKETS,
            position = Position(11.5697258, 104.9210242),
            phone = "012394300",
            address = "Central Market"
        ),
        Place(
            name = "Sivteang",
            category = Category.FOOD,
            position = Position(11.5452041, 104.9221723),
            phone = "099991201",
            address = "Wing Office"
        ),
        Place(
            name = "HS LPG",
            category = Category.OTHER,
            position = Position(11.597935, 104.872592),
            phone = "012594403",
            address = "Saensokh"
        ),
        Place(
            name = "Italy Coffee",
            category = Category.FOOD,
            position = Position(11.5195864, 104.916306),
            phone = "092886009",
            address = "Phsar PC"
        ),
        Place(
            name = "TK LPG",
            category = Category.OTHER,
            position = Position(11.5856121, 104.9009798),
            phone = "012826098",
            address = "Tuol Kouk Antenna"
        ),
        Place(
            name = "E183",
            category = Category.SUPERMARKETS,
            position = Position(11.5697258, 104.9210242),
            phone = "086776830",
            address = "Central Market"
        ),
        Place(
            name = "E022",
            category = Category.SUPERMARKETS,
            position = Position(11.5697258, 104.9210242),
            phone = "012786003",
            address = "Central Market"
        ),
        Place(
            name = "C081",
            category = Category.SUPERMARKETS,
            position = Position(11.5697258, 104.9210242),
            phone = "093205281",
            address = "Central Market"
        ),
        Place(
            name = "E176",
            category = Category.SUPERMARKETS,
            position = Position(11.5697258, 104.9210242),
            phone = "093816678",
            address = "Central Market"
        ),
        Place(
            name = "E149",
            category = Category.SUPERMARKETS,
            position = Position(11.5697258, 104.9210242),
            phone = "017570510",
            address = "Central Market"
        ),
        Place(
            name = "E076",
            category = Category.SUPERMARKETS,
            position = Position(11.5697258, 104.9210242),
            phone = "086602456",
            address = "Central Market"
        ),
        Place(
            name = "Punler Gas",
            category = Category.OTHER,
            position = Position(11.5727659, 104.8716363),
            phone = "087700202",
            address = "Saensokh"
        ),
        Place(
            name = "US Store",
            category = Category.SUPERMARKETS,
            position = Position(11.5656609, 104.9116406),
            phone = "077797920",
            address = "Baktouk"
        ),
        Place(
            name = "US Store 310",
            category = Category.SUPERMARKETS,
            position = Position(11.5508367, 104.917736),
            phone = "0979738888",
            address = "Tuo Sleng"
        ),
        Place(
            name = "Ly",
            category = Category.SUPERMARKETS,
            position = Position(11.5697258, 104.9210242),
            phone = "012971026",
            address = "Central Market"
        ),
        Place(
            name = "I2035",
            category = Category.SUPERMARKETS,
            position = Position(11.5697258, 104.9210242),
            phone = "093858680",
            address = "Central Market"
        ),
        Place(
            name = "I2138",
            category = Category.SUPERMARKETS,
            position = Position(11.5697258, 104.9210242),
            phone = "093878697",
            address = "Central Market"
        ),
        Place(
            name = "E003",
            category = Category.SUPERMARKETS,
            position = Position(11.5697258, 104.9210242),
            phone = "012391808",
            address = "Central Market"
        ),
        Place(
            name = "Huni Bee Beauty",
            category = Category.SUPERMARKETS,
            position = Position(11.5414985, 104.921966),
            phone = "012315445",
            address = "Boeng Trobek"
        ),
        Place(
            name = "Phum Café",
            category = Category.FOOD,
            position = Position(11.5394915, 104.9222866),
            phone = "085389495",
            address = "Boeng Trobek"
        ),
        Place(
            name = "Tuan Jet",
            category = Category.SUPERMARKETS,
            position = Position(11.5487773, 104.9115043),
            phone = "0717999919",
            address = "Tuol Svay Prey"
        ),
        Place(
            name = "Single Café",
            category = Category.FOOD,
            position = Position(11.5473095, 104.9067035),
            phone = "098226822",
            address = "Vanda"
        ),
        Place(
            name = "Rany Beauty Salon",
            category = Category.SUPERMARKETS,
            position = Position(11.5487773, 104.9115043),
            phone = "098916969",
            address = "Tuol Svay Prey"
        ),
        Place(
            name = "I Kids Home",
            category = Category.SUPERMARKETS,
            position = Position(11.5485085, 104.9051957),
            phone = "016500889",
            address = "Vanda"
        ),
        Place(
            name = "Mey Ly Salon",
            category = Category.SUPERMARKETS,
            position = Position(11.5447396, 104.9014774),
            phone = "016560679",
            address = "Sovanna Supermarket"
        ),
        Place(
            name = "Nik Pich Fashion",
            category = Category.SUPERMARKETS,
            position = Position(11.5378422, 104.921941),
            phone = "070364668",
            address = "Boeng Trobek"
        ),
        Place(
            name = "Genai Phone Shop",
            category = Category.SUPERMARKETS,
            position = Position(11.5417697, 104.9198654),
            phone = "011706000",
            address = "Tuol Tompong"
        ),
        Place(
            name = "Ptas Chhuer",
            category = Category.FOOD,
            position = Position(11.538668, 104.9218409),
            phone = "098704949",
            address = "Boeng Trobek"
        ),
        Place(
            name = "Por Por Salon",
            category = Category.SUPERMARKETS,
            position = Position(11.5388848, 104.9212222),
            phone = "077993994",
            address = "Boeng Trobek"
        ),
        Place(
            name = "A Nin Salon",
            category = Category.SUPERMARKETS,
            position = Position(11.5425267, 104.9195108),
            phone = "011558277",
            address = "Boeng Trobek"
        ),
        Place(
            name = "Somtam",
            category = Category.FOOD,
            position = Position(11.5425267, 104.9195108),
            phone = "093676452",
            address = "Boeng Trobek"
        ),
        Place(
            name = "Mealea Coffee",
            category = Category.FOOD,
            position = Position(11.5417697, 104.9198654),
            phone = "012782355",
            address = "Boeng Trobek"
        ),
        Place(
            name = "Oradent Dental",
            category = Category.PHARMACY,
            position = Position(11.5417777, 104.9201305),
            phone = "089900181",
            address = "Boeng Trobek"
        ),
        Place(
            name = "Konitha Salon",
            category = Category.SUPERMARKETS,
            position = Position(11.5337094, 104.9154932),
            phone = "089372407",
            address = "Tuol Tompong"
        ),
        Place(
            name = "Benz & Grace",
            category = Category.FOOD,
            position = Position(11.5357779, 104.9154005),
            phone = "070799109",
            address = "Tuol Tompong"
        ),
        Place(
            name = "Rith Store",
            category = Category.SUPERMARKETS,
            position = Position(11.5337376, 104.9167999),
            phone = "092844482",
            address = "Boeng Trobek"
        ),
        Place(
            name = "Find Me Over Here",
            category = Category.SUPERMARKETS,
            position = Position(11.5344189, 104.9215688),
            phone = "085742425",
            address = "Boeng Trobek"
        ),
        Place(
            name = "L&Y Collection",
            category = Category.SUPERMARKETS,
            position = Position(11.5418145, 104.9216808),
            phone = "0763838388",
            address = "Tuol Tompong"
        ),
        Place(
            name = "Clobber.G",
            category = Category.SUPERMARKETS,
            position = Position(11.5357779, 104.9154005),
            phone = "093253545",
            address = "Tuol Tompong"
        ),
        Place(
            name = "Ice Nature",
            category = Category.SUPERMARKETS,
            position = Position(11.5370565, 104.9153671),
            phone = "017893993",
            address = "Tuol Tompong"
        ),
        Place(
            name = "Minibucks",
            category = Category.SUPERMARKETS,
            position = Position(11.5370565, 104.9153671),
            phone = "012695566",
            address = "Tuol Tompong"
        ),
        Place(
            name = "Sovanna phon shop",
            category = Category.SUPERMARKETS,
            position = Position(11.5442337, 104.9176130),
            phone = "+85516366636",
            address = "Chamkar Morn "
        ),
        Place(
            name = "Brew cafe ",
            category = Category.FOOD,
            position = Position(11.5416620, 104.9138053),
            phone = "+85516533363",
            address = "Chamkar Morn "
        ),
        Place(
            name = "Hola Me",
            category = Category.FOOD,
            position = Position(11.541688, 104.919892),
            phone = "+85599641017",
            address = "St 432, Boeung Trobek"
        ),
        Place(
            name = "Sovannara phone shop ",
            category = Category.SUPERMARKETS,
            position = Position(11.5442380, 104.9176342),
            phone = "+85570888939",
            address = "Chamkar Morn "
        ),
        Place(
            name = "Nation cafe ",
            category = Category.FOOD,
            position = Position(11.5387505, 104.9168228),
            phone = "+85598388827",
            address = "#62B,St.135,Toul Tompong I, Chamkar Morn, Phnom Penh"
        ),
        Place(
            name = "Kafe ",
            category = Category.FOOD,
            position = Position(11.5426629, 104.9167537),
            phone = "+85585936136",
            address = "Street. 135, Boeung Kengkong, Chamkar Morn, Phnom Penh "
        ),
        Place(
            name = "24th",
            category = Category.FOOD,
            position = Position(11.563689, 104.898610),
            phone = "+85586780040",
            address = "St156, teuk laark 2, TK"
        ),
        Place(
            name = "The gray ",
            category = Category.SUPERMARKETS,
            position = Position(11.5642830, 104.9040893),
            phone = "+855969943735",
            address = "Toulkok "
        ),
        Place(
            name = "Premo cafe ",
            category = Category.FOOD,
            position = Position(11.5661825, 104.9037460),
            phone = "+85593313336",
            address = "Toulkok "
        ),
        Place(
            name = "Savoeung Phone Shop",
            category = Category.SUPERMARKETS,
            position = Position(11.539212, 104.919820),
            phone = "+85577881683",
            address = "Boeung Trabek"
        ),
        Place(
            name = "LY MEDIA",
            category = Category.SUPERMARKETS,
            position = Position(11.539206, 104.919830),
            phone = "+85510565551",
            address = "Boeung Kang Kong"
        ),
        Place(
            name = "TyTy collection ",
            category = Category.SUPERMARKETS,
            position = Position(11.5662752, 104.9021572),
            phone = "+85586434443",
            address = "Toulkok "
        ),
        Place(
            name = "C+ cafe ",
            category = Category.FOOD,
            position = Position(11.5660137, 104.9021779),
            phone = "+85517858516",
            address = "Toulkok "
        ),
        Place(
            name = "Cake Away",
            category = Category.FOOD,
            position = Position(11.562475, 104.925699),
            phone = "+85570777730",
            address = "Doun Penh "
        ),
        Place(
            name = "Qoutes cafe ",
            category = Category.FOOD,
            position = Position(11.5602083, 104.9213950),
            phone = "+85510424387",
            address = "Doun Penh "
        ),
        Place(
            name = "3 Plus ",
            category = Category.FOOD,
            position = Position(11.5678640, 104.9098294),
            phone = "+855962333444",
            address = "Toulkok "
        ),
        Place(
            name = "My cam",
            category = Category.SUPERMARKETS,
            position = Position(11.5527052, 104.9058697),
            phone = "+85570288222",
            address = "Chamkar Morn "
        ),
        Place(
            name = "S/M Coffee",
            category = Category.FOOD,
            position = Position(11.536608, 104.921475),
            phone = "+85569591919",
            address = "Boeung Tra Bek"
        ),
        Place(
            name = "Calito ",
            category = Category.FOOD,
            position = Position(11.5509218, 104.9193967),
            phone = "+85570885060",
            address = "Chamkar Morn"
        ),
        Place(
            name = "Jub Cafe",
            category = Category.FOOD,
            position = Position(11.5689060, 104.9160570),
            phone = "+85510874679",
            address = "Phsar Dumex Trafic Light"
        ),
        Place(
            name = "De Acrylic",
            category = Category.SUPERMARKETS,
            position = Position(11.541074, 104.912914),
            phone = "+85511962592",
            address = "N/A"
        ),
        Place(
            name = "Sbov cafe ",
            category = Category.FOOD,
            position = Position(11.5462011, 104.9161378),
            phone = "+85599784545",
            address = "Chamkar Morn "
        ),
        Place(
            name = "Labee shop ",
            category = Category.SUPERMARKETS,
            position = Position(11.5429819, 104.9117098),
            phone = "+85516555070",
            address = "Chamkar Morn"
        ),
        Place(
            name = "មីស.ខេ",
            category = Category.SUPERMARKETS,
            position = Position(11.534461, 104.914805),
            phone = "+85517345002",
            address = "N/A"
        ),
        Place(
            name = "TCC Phone shop ",
            category = Category.SUPERMARKETS,
            position = Position(11.5447144, 104.9160785),
            phone = "+85510394083",
            address = "Chamkar Morn "
        ),
        Place(
            name = "BIG CUP",
            category = Category.FOOD,
            position = Position(11.557805, 104.923585),
            phone = "+85515770956",
            address = "Phnom penh"
        ),
        Place(
            name = "My Style ",
            category = Category.SUPERMARKETS,
            position = Position(11.5641828, 104.9228000),
            phone = "",
            address = "#6E0, Street 178"
        ),
        Place(
            name = "Phum Bory Cake",
            category = Category.FOOD,
            position = Position(11.534343, 104.921454),
            phone = "+85586818193",
            address = "St 488"
        ),
        Place(
            name = "កាហ្វេ Amazon (សាខាបឹងកេងកង)",
            category = Category.FOOD,
            position = Position(11.5528, 104.9213),
            phone = "070652323",
            address = " House № 352A , St 39 ,  Sangkat Boeung Keng Kang 1 , Khan Chamkar Mong , Phnom Penh "
        ),
        Place(
            name = "កាហ្វេ Amazon (ច្បារអំពៅ)",
            category = Category.FOOD,
            position = Position(11.531601, 104.942425),
            phone = "098300113",
            address = "House № 627E0 , St.1 , Daeum Chan Village,  Sangkat Chbar Ampov 1 , Khan Chbar Ampov , Phnom Penh "
        ),
        Place(
            name = "Boutique Dental",
            category = Category.PHARMACY,
            position = Position(11.564469, 104.910776),
            phone = "012705310",
            address = "House №  #C2.1 , St. 162 ,  Phum 12, Sangkat Veal Vong, Khan 7 Makara, Phnom Penh"
        ),
        Place(
            name = "ភោជនីយដ្ឋានមាតុភូមិ (ផ្លូវ 271)",
            category = Category.FOOD,
            position = Position(11.5394564, 104.9098548),
            phone = "017882003",
            address = "Branch St. 271 : House № 265 ,  St.271 ,   Sangkat  Tuol Tompoung 2 , Khan Chamkar Mon , Phnom Penh "
        ),
        Place(
            name = "ហាងលក់សំលៀកបំពាក់ LOOK AT ME",
            category = Category.SUPERMARKETS,
            position = Position(11.540444, 104.911972),
            phone = "010636569",
            address = "Branch 1:  House № 1AE0E1 , St.167 , Sangkat Tuol Tompoung 2, Khan Chamkar Mon, Phnom Penh .  ( Tuol Tompoung)"
        ),
        Place(
            name = "ហាងលក់សំលៀកបំពាក់ LOOK AT ME",
            category = Category.SUPERMARKETS,
            position = Position(11.5343220, 104.9156416),
            phone = "010636569",
            address = "Branch 2:  House № 251 , St.155 , Sangkat Phsar Daem Thkov , Khan Chamkar Mon, Phnom Penh  ( Phsar Daem Thkov)"
        ),
        Place(
            name = "ហាងលក់សំលៀកបំពាក់ LOOK AT ME",
            category = Category.SUPERMARKETS,
            position = Position(11.5518610, 104.9230130),
            phone = "010636569",
            address = "Branch 3:  House № 55 , St.310 , Sangkat Boeung Keng Kang I , Khan Chamkar Mon, Phnom Penh . (Boeung Keng Kang)"
        ),
        Place(
            name = "បណ្ណាគារ កម្ពុជា",
            category = Category.SUPERMARKETS,
            position = Position(11.541677, 104.916206),
            phone = "023988887",
            address = " House № 44  St.432 ,  Sangkat Tuol Tompoung 1 , Khan Chamkar Mon, Phnom Penh "
        ),
        Place(
            name = "បណ្ណាគារ នគរធំ",
            category = Category.SUPERMARKETS,
            position = Position(11.5488645, 104.9077385),
            phone = "078933666",
            address = "House № 65AEO  , St 193, Sangkat Tuol Svay Prey 1 , Khan  Chamkar Mon, Phnom Penh"
        ),
        Place(
            name = "Mekong Elephants Coffee & Restaurant",
            category = Category.FOOD,
            position = Position(11.5347384, 104.9130344),
            phone = "015878579",
            address = "# 13, St. 430-506, Sangkat Phsar Derm Thkov, Khan Chomkamon, Phnom Penh"
        ),
        Place(
            name = "D & K Men Fashion Outlet",
            category = Category.SUPERMARKETS,
            position = Position(11.552402, 104.915960),
            phone = "098730828",
            address = " House № 176   St.143 ,  Sangkat  Olympic , Khan Chamkar Mon, Phnom Penh "
        ),
        Place(
            name = " D & K Coffee Shop",
            category = Category.FOOD,
            position = Position(11.552402, 104.916196),
            phone = "098730828",
            address = " House № 176   St.143 ,  Sangkat  Olympic , Khan Chamkar Mon, Phnom Penh "
        ),
        Place(
            name = "ហាងវ៉ែនតា ឡាណាណាអុបទិច",
            category = Category.SUPERMARKETS,
            position = Position(11.5317061, 104.9349606),
            phone = "012658465",
            address = "St.1 ,  Sangkat Chbar Ampov 1 , Khan Chbar Ampov , Phnom Penh "
        ),
        Place(
            name = "អាហារដ្ឋានភ្នំពេញមីស្ទ័រក្រេប",
            category = Category.FOOD,
            position = Position(11.5405674, 104.9099093),
            phone = "077323135",
            address = "House № 85, St.430 corner 450 , Sangkat Tuol Tompoung 2, Khan Chamkar Mon, Phnom Penh"
        ),
        Place(
            name = "ភោជនីយដ្ឋាននគររាជ",
            category = Category.FOOD,
            position = Position(11.5385014, 104.9107359),
            phone = "077849385",
            address = "House № 481 ,  St.430 ,   Sangkat  Tuol Tompoung 2 , Khan Chamkar Mon , Phnom Penh "
        ),
        Place(
            name = "Se7en Days Coffee Shop",
            category = Category.FOOD,
            position = Position(11.541276, 104.909733),
            phone = "078346333",
            address = " House № 126  St.430 ,  Sangkat Tuol Tompoung 2 , Khan Chamkar Mon, Phnom Penh "
        ),
        Place(
            name = "Classic Coffee Shop",
            category = Category.FOOD,
            position = Position(11.5338267, 104.9119511),
            phone = "0717171771",
            address = "House № 504Y, St.271 , Sangkat Tuol Tompoung 2, Khan Chamkar Mon, Phnom Penh"
        ),
        Place(
            name = "BB Coffee Coffee Shop",
            category = Category.FOOD,
            position = Position(11.5386393, 104.5405674),
            phone = "093465445",
            address = "House № 504, St.271 , Sangkat Tuol Tompoung 2, Khan Chamkar Mon, Phnom Penh"
        ),
        Place(
            name = "អាហាដ្ឋានម្លប់មៀន",
            category = Category.FOOD,
            position = Position(11.5400323, 104.9104828),
            phone = "015546462",
            address = "House № 5E0, St.450 , Sangkat Tuol Tompoung 2, Khan Chamkar Mon, Phnom Penh"
        ),
        Place(
            name = "ហាងលក់គ្រឿងលំអឡាន ងិត សុធី",
            category = Category.SUPERMARKETS,
            position = Position(11.542021, 104.910411),
            phone = "012222392",
            address = "House № 85 C, St. 426  , Sangkat Tuol Tompoung 2, Khan Chamkar Mon, Phnom Penh"
        ),
        Place(
            name = "អារហារដ្ឋានលក់បាយម៉ាក់រីតា",
            category = Category.FOOD,
            position = Position(11.542210, 104.909785),
            phone = "0886008381",
            address = "House № 179 , St. 173 , Sangkat  Tumnob TeukI , Khan Chamkar Mon, Phnom Penh"
        ),
        Place(
            name = "អារហារដ្ឋានបាយមាន់ស្រែ",
            category = Category.FOOD,
            position = Position(11.5400067, 104.9102021),
            phone = "087717202",
            address = "House № 150 , St. 430 , Sangkat  Tuol Tompoung 2 , Khan Chamkar Mon, Phnom Penh"
        ),
        Place(
            name = "អារហារដ្ឋានផ្ទះបាយក្រៃថោង",
            category = Category.FOOD,
            position = Position(11.541386, 104.910319),
            phone = "011817456",
            address = "House № 239 , St. 39 , Sangkat  Boeung Tumpun , Khan Mean Chey, Phnom Penh"
        ),
        Place(
            name = "ហាងយក្សាបោកអ៊ុត",
            category = Category.OTHER,
            position = Position(11.542666, 104.910248),
            phone = "095569333",
            address = "House № 170 , St. 173 , Sangkat  Tuol Tompoung 1 , Khan Mean Chey, Phnom Penh"
        ),
        Place(
            name = "ហាងលក់គឿងសំណង់ KT ",
            category = Category.SUPERMARKETS,
            position = Position(11.5273296, 104.9168882),
            phone = "093883550",
            address = """Branch 1:  No182DEo St 107 , Sangkat Orussey 4, Khan 7 Makara, Phnom Penh 
    Branch 2:  No 43C , St. 271, Sangkat Phsar Daem Thkov , Khan Chamkar Mon , Phnom Penh Branch 3:  No 363-365 ST 245 (Mao Se Tong Blvd) Sangkat Phsar Depo II , Khan Toul Kork ,Phnom Penh
    """
        ),
        Place(
            name = "អាហារដ្ឋាន ហ៊ា សុខចេង ",
            category = Category.FOOD,
            position = Position(11.5306913, 104.9282349), phone = "012575702",
            address = "Phrase Norotherm Blvd, Sangkat Tonle Bassac , Khan Chamkar Mon , Phnom Penh "
        ), Place(
            name = "យានដ្ឋាន វ៉ាអូតូ 99",
            category = Category.OTHER,
            position = Position(11.5230997, 104.9153345), phone = "017300036 / 015300037",
            address = " House № 155 St.69BT ,  Sangkat  Boeung Tumpun , Khan Mean Chey, Phnom Penh "
        ),
        Place(
            name = "Pick Up Coffee Shop",
            category = Category.FOOD,
            position = Position(11.5294045, 104.9158478), phone = "089853878",
            address = "St.430 , Sangkat  Phsar Daem Thkov , Khan Chamkar Mon , Phnom Penh "
        ),
        Place(
            name = "ហាង ហុង ស្រីណា", category = Category.SUPERMARKETS,
            position = Position(11.6611359, 104.8609564), phone = "092333992",
            address = "Kandal Village ,  Sangkat  Prek Pnov , Khan Prek Pnov, Phnom Penh "
        ),
        Place(
            name = "Healthy Coffee Shop",
            category = Category.FOOD,
            position = Position(11.525355, 104.881067),
            phone = "010369644",
            address = "Mol Village, Sangkat Dangkao, Khan  Dangkao, Phnom Penh"
        ),
        Place(
            name = "Bo The Grace Coffee shop",
            category = Category.FOOD,
            position = Position(11.520434, 104.8855),
            phone = "016887027",
            address = "Mol Village, Sangkat Dangkao, Khan  Dangkao, Phnom Penh"
        ),
        Place(
            name = "ហាង ណានីតា បុកល្ហុង",
            category = Category.FOOD,
            position = Position(11.520259, 104.885570), phone = "0967782616",
            address = "Tol Pong Ro Village, Sangkat Dangkao, Khan  Dangkao, Phnom Penh"
        ),
        Place(
            name = "Walker café Shop",
            category = Category.FOOD,
            position = Position(11.518539, 104.899244), phone = "070800011",
            address = "NA-Khva/Dangkao/Dangkao/Phnom Penh"
        ),
        Place(
            name = "ហាងកាហ្វេ បុស្បា ",
            category = Category.FOOD,
            position = Position(11.563162, 104.862494),
            phone = "0965778798",
            address = "PorPrork Khang Cheung Village,  Sangkat  Kakab , Khan Pou Senchey , Phnom Penh "
        ),
        Place(
            name = "ហេង ហេង ចោមចៅ លក់ទូរស័ព្ទ",
            category = Category.SUPERMARKETS,
            position = Position(11.533809, 104.829814),
            phone = "089709000",
            address = "Chaom Chau Village, Sangkat Chaom Chau , Khan Pou Senchey , Phnom Penh "
        ),
        Place(
            name = "Dami Fashion Shop",
            category = Category.SUPERMARKETS,
            position = Position(11.575647, 104.900443),
            phone = "0962126411",
            address = "Pencil Market, Phnom Penh.    "
        ),
        Place(
            name = " Mr.កន Coffee and Bakery Shop",
            category = Category.FOOD,
            position = Position(11.548956, 104.921420),
            phone = "069222995",
            address = "Branch 2: House № 49 , St.95,  Sangkat Beung Kengkan 3 , Khan Beung Kengkang , Phnom Penh "
        ),
        Place(
            name = "អាហារដ្ឋានជ័យជំនះត្រជាក់ចិត្ត",
            category = Category.FOOD,
            position = Position(11.544525, 104.919458),
            phone = "017676716 / 012278696",
            address = " House № 73 St. 105 ,  Sangkat Boeng Keng Kang 3 , Khan Boeng Keng Kang, Phnom Penh "
        ),
        Place(
            name = "ហាងលក់ទូរស័ព្ទដៃ ម៉ាយហ្វូន",
            category = Category.SUPERMARKETS,
            position = Position(11.542845, 104.915321),
            phone = "098701118",
            address = " House № 11c   St.155 ,  Sangkat Tuol Tompoung 1 , Khan Chamkar Mon, Phnom Penh "
        ),
        Place(
            name = "ហាងកាហ្វេ ស្បូវកាហ្វេ",
            category = Category.FOOD,
            position = Position(11.477133, 104.947167),
            phone = "081426895",
            address = "House № 322, St.201 , Sangkat Takhmao, Khan Takhmao, Kandal"
        ),
        Place(
            name = "ហាងសាឡន In Style",
            category = Category.SUPERMARKETS,
            position = Position(11.543519, 104.919478),
            phone = "011242467",
            address = "House № 143 , St 105, Sangkat Boeung Trobaek , Khan  Chamkar Mon, Phnom Penh"
        ),
        Place(
            name = "ឱសថស្ថាន ឱសថអមតះ កំលាំងសេះខ្មៅ សច្ចំប្រទាន",
            category = Category.PHARMACY,
            position = Position(11.543927, 104.917769),
            phone = "089708797",
            address = "House № 96c, St.245 , Sangkat Boeng Trabaek, Khan Chamkar Mon, Phnom Penh "
        ),
        Place(
            name = "TCC Phone Shop",
            category = Category.SUPERMARKETS,
            position = Position(11.5444668, 104.916065),
            phone = "070983838",
            address = "#154, St143, Boeung Kengkang III, Chamkaromon, Phnom Penh"
        ),
        Place(
            name = " ហាង Lim Ham II លក់វិទ្យុទាក់ទង",
            category = Category.SUPERMARKETS,
            position = Position(11.541565, 104.912577),
            phone = "016443644",
            address = " House № 98B  St 432 ,  Sangkat Tuol Tompoung 2 , Khan Chamkar Mong , Phnom Penh "
        ),
        Place(
            name = "Discount Furniture Shop",
            category = Category.SUPERMARKETS,
            position = Position(11.550805, 104.919674),
            phone = "011766688",
            address = "#440, St310, Boeung Kengkang III, Chamkaromon, Phnom Penh"
        ),
        Place(
            name = "ហាងលក់កាហ្វេ រីករាយកាហ្វេដូងក្រអូប",
            category = Category.FOOD,
            position = Position(11.5546435, 104.8871693),
            phone = "012914340",
            address = "  St.271 , Phum 11,   Sangkat Boeung Salang , Khan Tuol Kouk , Phnom Penh "
        ),
        Place(
            name = "ហាងរាជសូលីកាវីង",
            category = Category.SUPERMARKETS,
            position = Position(11.6588, 104.7748),
            phone = "093505006",
            address = "Boeng Village ,  Sangkat  Ponhea Pon , Khan Prek Pnov, Phnom Penh "
        ),
        Place(
            name = "ជាងទុំកាត់ដេរសំលៀកបំពាក់",
            category = Category.SUPERMARKETS,
            position = Position(11.6598157, 104.8560952),
            phone = "0712272967",
            address = "Prek Pnov Village ,  Sangkat  Prek Pnov , Khan Prek Pnov, Phnom Penh "
        ),
        Place(
            name = "ហាងលក់បាយម្ហូបការម្មង់ កុល សុធា  ",
            category = Category.FOOD,
            position = Position(11.531678, 104.939159),
            phone = "012422830",
            address = "St.1 ,  Daeum Chan Village, Sangkat Chbar Ampov  2, Khan  Chbar Ampov, Phnom Penh"
        ),
        Place(
            name = "ហាងលក់សម្ភារៈផ្ទះបាយ និងគ្រឿងអគ្គីសនីគ្រប់ប្រភេទ ",
            category = Category.SUPERMARKETS,
            position = Position(11.6607577, 104.8603209),
            phone = "099666611",
            address = "Kandal Village ,  Sangkat  Prek Pnov , Khan Prek Pnov, Phnom Penh "
        ),
        Place(
            name = "ហាងកាហ្វេ ម៉ួស្ទីនកាហ្វេ",
            category = Category.FOOD,
            position = Position(11.5400514, 104.9101304),
            phone = "017543740",
            address = " St.430 , Sangkat Tuol Tompoung 2, Khan Chamkar Mon, Phnom Penh "
        ),
        Place(
            name = "Sithina Phone Shop",
            category = Category.OTHER,
            position = Position(11.563395, 104.911771),
            phone = "016 777 736",
            address = "Phnom Penh"
        ),
        Place(
            name = "THY SOPHAL",
            category = Category.OTHER,
            position = Position(11.564049, 104.910504),
            phone = "012 790 200",
            address = "Phnom Penh"
        ),
        Place(
            name = "Signature Coffee and Pub",
            category = Category.OTHER,
            position = Position(11.580647, 104.900770),
            phone = "099 900 900",
            address = "Phnom Penh"
        ),
        Place(
            name = "Café klang",
            category = Category.OTHER,
            position = Position(11.5525224, 104.8756224),
            phone = "070 656 316",
            address = "Phnom Penh"
        ),
        Place(
            name = "Hot Pot Station",
            category = Category.OTHER,
            position = Position(11.5628104, 104.9112117),
            phone = "069 528 000",
            address = "Phnom Penh"
        ),
        Place(
            name = "Kabass Restaurant",
            category = Category.OTHER,
            position = Position(11.566861, 104.926972),
            phone = "012 807 979",
            address = "Phnom Penh"
        ),
        Place(
            name = "Home Sport Sonthormuk ",
            category = Category.OTHER,
            position = Position(11.568279, 104.897198),
            phone = "092 681 330",
            address = "Phnom Penh"
        ),
        Place(
            name = "See You Soon",
            category = Category.OTHER,
            position = Position(13.098782, 103.202758),
            phone = "086 616 216",
            address = "Battambang"
        ),
        Place(
            name = "Tao Huo Ang",
            category = Category.OTHER,
            position = Position(13.092383, 103.202931),
            phone = "093 929 222",
            address = "Battambang"
        ),
        Place(
            name = "Hong Try Optic 2",
            category = Category.OTHER,
            position = Position(13.100945, 103.197361),
            phone = "012 814 514",
            address = "Battambang"
        ),
        Place(
            name = "Piphop Nom Bakery",
            category = Category.OTHER,
            position = Position(13.10515, 103.199254),
            phone = "093 737 485",
            address = "Battambang"
        ),
        Place(
            name = "Espresso.Plus ",
            category = Category.OTHER,
            position = Position(13.098938, 103.198788),
            phone = "012 910 690",
            address = "Battambang"
        ),
        Place(
            name = "Ma Cherie Café",
            category = Category.OTHER,
            position = Position(13.103671, 103.213586),
            phone = "093 929 222",
            address = "Battambang"
        ),
        Place(
            name = "Khmer Optic",
            category = Category.OTHER,
            position = Position(13.100310, 103.197357),
            phone = "087 282 844",
            address = "Battambang"
        ),
        Place(
            name = "Chan Neang Jewelry",
            category = Category.OTHER,
            position = Position(13.102095, 103.198493),
            phone = "070 243 424",
            address = "Battambang"
        ),
        Place(
            name = "Orchid Café",
            category = Category.OTHER,
            position = Position(12.963675, 103.049437),
            phone = "012 412 212",
            address = "Battambang"
        ),
        Place(
            name = "T & V Barber Nisset",
            category = Category.OTHER,
            position = Position(11.568317, 104.893208),
            phone = "077 490 046",
            address = "Phnom Penh"
        ),
        Place(
            name = "Emma17 Café",
            category = Category.OTHER,
            position = Position(11.566789, 104.908569),
            phone = "012 233 626",
            address = "Phnom Penh"
        ),
        Place(
            name = "Always more Coffee",
            category = Category.OTHER,
            position = Position(11.563314, 104.911903),
            phone = "096 8098398 ",
            address = "Phnom Penh"
        ),
        Place(
            name = "TIT SREYNAK",
            category = Category.OTHER,
            position = Position(11.563098, 104.911308),
            phone = "012 861 409",
            address = "Phnom Penh"
        ),
        Place(
            name = "Vanreach",
            category = Category.OTHER,
            position = Position(11.558137, 104.894251),
            phone = "016 522252",
            address = "Phnom Penh"
        ),
        Place(
            name = "Piseth Phone Shop",
            category = Category.OTHER,
            position = Position(11.566677, 104.906611),
            phone = "016 777 736",
            address = "Phnom Penh"
        ),
        Place(
            name = "Som Malin",
            category = Category.OTHER,
            position = Position(11.563158, 104.910802),
            phone = "086 985 608",
            address = "Phnom Penh"
        ),
        Place(
            name = "Hi Store",
            category = Category.OTHER,
            position = Position(11.559595, 104.915270),
            phone = "081 529 006",
            address = "Phnom Penh"
        ),
        Place(
            name = "Leng Derluxe Coffee",
            category = Category.OTHER,
            position = Position(11.529400, 104.944363),
            phone = "017 333 779",
            address = "Phnom Penh"
        ),
        Place(
            name = "4B",
            category = Category.OTHER,
            position = Position(11.576950, 104.898594),
            phone = "093 777 502",
            address = "Phnom Penh"
        ),
        Place(
            name = "Tech Cafe",
            category = Category.OTHER,
            position = Position(11.561941, 104.920323),
            phone = "098 700 088",
            address = "Phnom Penh"
        ),
        Place(
            name = "Fong Fong",
            category = Category.OTHER,
            position = Position(11.538968, 104.919521),
            phone = "070 720 678",
            address = "Phnom Penh"
        ),
        Place(
            name = "Cocoly Coffee",
            category = Category.OTHER,
            position = Position(11.568117, 104.898210),
            phone = "076 770 6666",
            address = "Phnom Penh"
        ),
        Place(
            name = "Julie Café",
            category = Category.OTHER,
            position = Position(11.567360, 104.912752),
            phone = "012 823 388",
            address = "Phnom Penh"
        ),
        Place(
            name = "Phy Na Restaurant",
            category = Category.OTHER,
            position = Position(11.996974, 105.457511),
            phone = "090 928 181",
            address = "Kompong Cham"
        ),
        Place(
            name = "Leang Sry Sell Beverage",
            category = Category.OTHER,
            position = Position(11.989827, 105.465852),
            phone = "012 941 236",
            address = "Kompong Cham"
        ),
        Place(
            name = "Oyster Trachakchett",
            category = Category.OTHER,
            position = Position(11.990657, 105.456491),
            phone = "092 420 103 ",
            address = "Kompong Cham"
        ),
        Place(
            name = "Kimsrean Cosmetic",
            category = Category.OTHER,
            position = Position(11.989816, 105.465602),
            phone = "012 427818",
            address = "Kompong Cham"
        ),
        Place(
            name = "KIK Cake Shop",
            category = Category.OTHER,
            position = Position(11.990722, 105.465583),
            phone = "012 696 921",
            address = "Kompong Cham"
        ),
        Place(
            name = "Best Style",
            category = Category.OTHER,
            position = Position(11.990107, 105.465503),
            phone = "012 937 663",
            address = "Kompong Cham"
        ),
        Place(
            name = "Piphup Thmey II Restaurant",
            category = Category.OTHER,
            position = Position(11.997288, 105.455999),
            phone = "012 783 107",
            address = "Kompong Cham"
        ),
        Place(
            name = "Roman Optic",
            category = Category.OTHER,
            position = Position(11.991336, 105.455314),
            phone = "099 576 799",
            address = "Kompong Cham"
        ),
        Place(
            name = "Amazon Eye Optic",
            category = Category.OTHER,
            position = Position(11.991643, 105.459355),
            phone = "010 87 27 87",
            address = "Kompong Cham"
        ),
        Place(
            name = "Chin Sayhiek ",
            category = Category.OTHER,
            position = Position(11.9898545, 105.466371),
            phone = "012 811 350 ",
            address = "Kompong Cham"
        ),
        Place(
            name = "Kheang Chanda",
            category = Category.OTHER,
            position = Position(11.9912675, 105.455287),
            phone = "012 798 727",
            address = "Kompong Cham"
        ),
        Place(
            name = "Keat Sereysophea",
            category = Category.OTHER,
            position = Position(11.991734, 105.464886),
            phone = "098 888 257",
            address = "Kompong Cham"
        ),
        Place(
            name = "Kheng Kunthea",
            category = Category.OTHER,
            position = Position(11.99601, 105.457195),
            phone = "017 971 027",
            address = "Kompong Cham"
        ),
        Place(
            name = "Hao An Restaurant ",
            category = Category.OTHER,
            position = Position(11.989639, 105.462762),
            phone = "017 366 616",
            address = "Kompong Cham"
        ),
        Place(
            name = "UKC Food and Coffee",
            category = Category.OTHER,
            position = Position(11.983057, 105.444885),
            phone = "011 209 747",
            address = "Kompong Cham"
        ),
        Place(
            name = "Mao Heng",
            category = Category.OTHER,
            position = Position(11.990551, 105.466409),
            phone = "012 564 173",
            address = "Kompong Cham"
        ),
        Place(
            name = "MOOD",
            category = Category.OTHER,
            position = Position(11.990102, 105.459933),
            phone = "070 797 676",
            address = "Kompong Cham"
        )
        // endregion
    )