package com.example.data

data class Product(
    val id: String,
    val name: String,
    val brand: String,
    val category: String,
    val price: Int,
    val prize: String,
    val isEatable: Boolean,
    val isFssaiCertified: Boolean,
    val fssaiLicenseNo: String = "",
    val fssaiRating: Double = 0.0,
    val nutrition: Map<String, String> = emptyMap(),
    val allergens: List<String> = emptyList(),
    val ingredients: List<String> = emptyList()
)

object MockProducts {
    val list = listOf(
        Product(
            id = "8901031170024",
            name = "Premium Alphonso Mango Juice",
            brand = "Maaza Organic",
            category = "Beverages",
            price = 95,
            prize = "Flat ₹20 Cashback on your next purchase!",
            isEatable = true,
            isFssaiCertified = true,
            fssaiLicenseNo = "11523034000182",
            fssaiRating = 4.8,
            nutrition = mapOf(
                "Energy" to "120 kcal",
                "Carbohydrates" to "28g",
                "Sugar" to "24g",
                "Vitamin C" to "45mg",
                "Fat" to "0.1g",
                "Protein" to "0.4g"
            ),
            allergens = listOf("Gluten-Free", "Vegan", "No Artificial Preservatives"),
            ingredients = listOf("Alphonso Mango Pulp (45%)", "Water", "Organic Cane Sugar", "Citric Acid", "Pectin")
        ),
        Product(
            id = "8901725181224",
            name = "Masala Potato Chips",
            brand = "Haldiram's",
            category = "Snacks",
            price = 30,
            prize = "Flat 10% Off on grocery billing!",
            isEatable = true,
            isFssaiCertified = true,
            fssaiLicenseNo = "10014022002759",
            fssaiRating = 4.1,
            nutrition = mapOf(
                "Energy" to "160 kcal",
                "Fats" to "10g",
                "Saturated Fat" to "4.2g",
                "Sodium" to "180mg",
                "Carbohydrates" to "15g",
                "Protein" to "2.1g"
            ),
            allergens = listOf("Contains Soy", "May contain traces of Peanuts", "Gluten-Free"),
            ingredients = listOf("Selected Potatoes", "Refined Palmolein Oil", "Salt", "Spices & Condiments (Chilli, Onion, Garlic)", "Acidity Regulator (E330)")
        ),
        Product(
            id = "8901200000015",
            name = "Pure Cow Ghee (A2 Organic)",
            brand = "Amul Organic",
            category = "Dairy & Fats",
            price = 720,
            prize = "Exclusive Market Coupon: GHEE50 (₹50 discount!)",
            isEatable = true,
            isFssaiCertified = true,
            fssaiLicenseNo = "12218026000249",
            fssaiRating = 4.9,
            nutrition = mapOf(
                "Energy" to "897 kcal",
                "Total Fat" to "99.7g",
                "Saturated Fat" to "65g",
                "Cholesterol" to "256mg",
                "Vitamin A" to "1.2mg"
            ),
            allergens = listOf("Contains Milk Lactose", "Gluten-Free"),
            ingredients = listOf("Pure Clarified Butter Fat (derived from fresh Organic A2 Cow Milk)")
        ),
        Product(
            id = "8906001053241",
            name = "Citrus Glow Hand Sanitizer Gel",
            brand = "Dettol Guard",
            category = "Personal Care",
            price = 120,
            prize = "Buy 2 Get 1 Free on all Personal Care essentials!",
            isEatable = false,
            isFssaiCertified = false,
            nutrition = mapOf(
                "Active Ingredients" to "70% v/v Ethyl Alcohol",
                "Skin Conditioning" to "Aloe Vera & Glycerin",
                "pH Level" to "6.5 (Skin Friendly)",
                "Hazard Status" to "Flammable"
            ),
            allergens = listOf("FOR EXTERNAL USE ONLY", "Flammable - Keep away from fire", "Avoid contact with eyes"),
            ingredients = listOf("Ethanol (70%)", "Purified Water", "Glycerin", "Carbomer", "Triethanolamine", "Aloe Barbadensis Leaf Juice", "Citrus Essential Fragrance")
        ),
        Product(
            id = "8901030752498",
            name = "Ultra Shine Laundry Liquid",
            brand = "Surf Excel",
            category = "Household Utilities",
            price = 245,
            prize = "Free 100ml Fabric Softener trial pack with your purchase!",
            isEatable = false,
            isFssaiCertified = false,
            nutrition = mapOf(
                "Surfactant Agent" to "Anionic & Non-ionic Active Agents",
                "Stain Fighter" to "Multi-Enzyme Power Core",
                "Safety Status" to "Skin-Safe Detergent Formulation"
            ),
            allergens = listOf("DO NOT INGEST", "Keep out of reach of children", "If swallowed, drink plenty of water and seek medical help"),
            ingredients = listOf("Sodium Carbonate", "Alkylbenzene Sulfonic Acid", "Sodium Laureth Sulfate", "Enzymes", "Optical Brighteners", "Fresh Blossom Perfume")
        )
    )

    fun findById(id: String): Product? {
        return list.find { it.id == id || it.id.contains(id) || id.contains(it.id) }
    }
}
