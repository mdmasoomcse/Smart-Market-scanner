package com.example.data

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiApiClient {
    private const val TAG = "GeminiApiClient"
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun analyzeProduct(query: String): Product? = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            Log.w(TAG, "GEMINI_API_KEY is not defined in BuildConfig", e)
            ""
        }

        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.w(TAG, "Gemini API key is blank/default. Using simulated smart product generator.")
            return@withContext simulateSmartProduct(query)
        }

        val prompt = """
            You are an expert product scanning system. Analyze the following QR code scan input or product name: "$query".
            Determine if it represents a physical, eatable (edible/drinkable) food product, consumer good, or non-food product.
            Return a structured JSON object containing:
            - "name": The identified or inferred name of the product (e.g., "Amul Gold Pasteurised Milk", "Tata Iodized Salt", "Surf Excel Easy Wash"). Keep it clean and literal.
            - "brand": The brand name (e.g., "Amul", "Tata", "Surf Excel", or "Generic").
            - "category": Category of the product (e.g., Beverages, Snacks, Dairy, Personal Care, Household, Packaged Foods, Baked Goods).
            - "price": An estimated or typical price in Indian Rupees (INR) as an integer (e.g., 45, 120, 10).
            - "prize": A fun, marketing-style 'lucky scratch prize' or reward discount code for scanning this in a market (e.g., "Flat ₹10 Off", "Buy 1 Get 1 Free Coupon", "₹15 Cashback").
            - "isEatable": Boolean indicating if this is an edible/drinkable food/beverage product.
            - "isFssaiCertified": Boolean. If it is edible, return true. If not edible, return false.
            - "fssaiLicenseNo": If edible, a realistic 14-digit FSSAI license number (e.g., "12421002000341"). If not edible, empty string.
            - "fssaiRating": If edible, a rating between 1.0 and 5.0 (e.g., 4.7). If not edible, 0.0.
            - "nutrition": A simple flat JSON map of nutrients (key-value strings) like "Calories", "Protein", "Carbohydrates", "Fat", "Sodium", "Sugar". For non-edible items, use safety/chemical components like "Active Ingredients", "pH Level", "Hazard Status".
            - "allergens": A list of allergens (e.g., "Nuts", "Gluten", "Dairy") or hazard warnings (e.g., "Not for ingestion", "Keep away from eyes").
            - "ingredients": A list of primary ingredients (e.g., "Pasteurized Milk", "Iodized Salt").

            Ensure your response is valid JSON and ONLY the JSON itself. Do not wrap it in markdown block quotes or any text.
        """.trimIndent()

        val requestJson = JSONObject().apply {
            put("contents", JSONArray().put(
                JSONObject().apply {
                    put("parts", JSONArray().put(
                        JSONObject().apply {
                            put("text", prompt)
                        }
                    ))
                }
            ))
            put("generationConfig", JSONObject().apply {
                put("responseMimeType", "application/json")
            })
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = requestJson.toString().toRequestBody(mediaType)
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"
        
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.e(TAG, "Request failed with code: ${response.code}")
                    return@withContext simulateSmartProduct(query)
                }
                
                val responseBodyString = response.body?.string() ?: return@withContext simulateSmartProduct(query)
                val responseJson = JSONObject(responseBodyString)
                val candidates = responseJson.getJSONArray("candidates")
                if (candidates.length() > 0) {
                    val candidate = candidates.getJSONObject(0)
                    val content = candidate.getJSONObject("content")
                    val parts = content.getJSONArray("parts")
                    if (parts.length() > 0) {
                        val text = parts.getJSONObject(0).getString("text")
                        return@withContext parseJsonToProduct(query, text)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during Gemini API call", e)
        }
        
        simulateSmartProduct(query)
    }

    private fun parseJsonToProduct(query: String, jsonStr: String): Product? {
        return try {
            val json = JSONObject(jsonStr)
            val nutritionMap = mutableMapOf<String, String>()
            val nutritionJson = json.optJSONObject("nutrition")
            if (nutritionJson != null) {
                val keys = nutritionJson.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    nutritionMap[key] = nutritionJson.getString(key)
                }
            }

            val allergensList = mutableListOf<String>()
            val allergensArray = json.optJSONArray("allergens")
            if (allergensArray != null) {
                for (i in 0 until allergensArray.length()) {
                    allergensList.add(allergensArray.getString(i))
                }
            }

            val ingredientsList = mutableListOf<String>()
            val ingredientsArray = json.optJSONArray("ingredients")
            if (ingredientsArray != null) {
                for (i in 0 until ingredientsArray.length()) {
                    ingredientsList.add(ingredientsArray.getString(i))
                }
            }

            Product(
                id = query.hashCode().toString(),
                name = json.optString("name", query),
                brand = json.optString("brand", "Unknown Brand"),
                category = json.optString("category", "General"),
                price = json.optInt("price", 49),
                prize = json.optString("prize", "Lucky Scratch Prize: Flat ₹10 Off!"),
                isEatable = json.optBoolean("isEatable", true),
                isFssaiCertified = json.optBoolean("isFssaiCertified", true),
                fssaiLicenseNo = json.optString("fssaiLicenseNo", "10022011003451"),
                fssaiRating = json.optDouble("fssaiRating", 4.5),
                nutrition = nutritionMap,
                allergens = allergensList,
                ingredients = ingredientsList
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse Gemini JSON output. Using fallback generator.", e)
            simulateSmartProduct(query)
        }
    }

    private fun simulateSmartProduct(query: String): Product {
        // Detect if query looks like non-food or food
        val queryLower = query.lowercase()
        val isFoodKeywords = listOf("milk", "chips", "juice", "ghee", "bread", "salt", "chocolate", "biscuit", "water", "soda", "rice", "curd", "paneer", "oil", "food")
        val isNonFoodKeywords = listOf("soap", "shampoo", "sanitizer", "detergent", "cleaner", "gel", "battery", "spray", "perfume", "cream", "paste", "chemical")
        
        var looksLikeFood = true
        for (kw in isNonFoodKeywords) {
            if (queryLower.contains(kw)) {
                looksLikeFood = false
                break
            }
        }
        
        // Capitalize words in query for clean name
        val capitalizedQuery = query.split(" ").joinToString(" ") { word ->
            word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }

        return if (looksLikeFood) {
            val randomLicense = "1" + (1000000000000L..9999999999999L).random().toString()
            val randomPrice = (20..350).random()
            Product(
                id = query.hashCode().toString(),
                name = capitalizedQuery,
                brand = "Pure & Natural Ltd",
                category = "Packaged Foods",
                price = randomPrice,
                prize = "Aesthetic Market Prize: ₹${(5..35).random()} Cash Back on scan!",
                isEatable = true,
                isFssaiCertified = true,
                fssaiLicenseNo = randomLicense,
                fssaiRating = (4.0 + (0..10).random() * 0.1).coerceAtMost(5.0),
                nutrition = mapOf(
                    "Energy" to "${(50..450).random()} kcal",
                    "Carbohydrates" to "${(5..60).random()}g",
                    "Protein" to "${(1..15).random()}g",
                    "Total Fat" to "${(0..25).random()}g",
                    "Sodium" to "${(10..200).random()}mg"
                ),
                allergens = listOf("Gluten-Free", "Vegan Approved"),
                ingredients = listOf("Natural Ingredients", "Filtered Water", "Organic Spices", "Vitamins & Minerals Added")
            )
        } else {
            val randomPrice = (40..500).random()
            Product(
                id = query.hashCode().toString(),
                name = capitalizedQuery,
                brand = "EcoGuard & Co.",
                category = "Home & Personal Care",
                price = randomPrice,
                prize = "Exclusive Prize: Flat 15% discount coupon locked!",
                isEatable = false,
                isFssaiCertified = false,
                nutrition = mapOf(
                    "Active Agents" to "Surfactants and Conditioning Esters",
                    "pH Level" to "5.5 (Skin Balanced)",
                    "Hazard Rating" to "Non-Toxic / Biodegradable"
                ),
                allergens = listOf("FOR EXTERNAL USE ONLY", "Keep out of reach of children", "Avoid contact with eyes"),
                ingredients = listOf("Saponified Vegetable Oils", "Aqua", "Essential Oils for Fragrance", "Organic Stabilizers")
            )
        }
    }
}
