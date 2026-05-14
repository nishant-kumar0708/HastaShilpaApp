package com.hastashilpa.app.util

/**
 * Search queries for Unsplash API.
 * Queries are broad enough to always return results.
 */
object ProductImageUrls {

    // ── Per-product queries — broad enough to always return images ─────────
    private val productQueries = mapOf(
        // FURNITURE
        "Rattan Bloom Chair"        to "wicker lounge chair natural",
        "Cane Peacock Chair"        to "wicker peacock chair",
        "Bamboo Lounge Stool"       to "bamboo stool",
        "Cane Bar Stool"            to "rattan stool colorful",
        "Cane Dining Table"         to "rattan dining table",
        "Bamboo Bookshelf"          to "bamboo shelf",
        "Bamboo Ladder Shelf"       to "wooden ladder shelf",
        "Cane Headboard"            to "rattan headboard",

        // BASKETS — fixed: "wicker storage trunk" was too narrow
        "Cane Storage Trunk"        to "handwoven rattan storage basket",
        "Bamboo Flower Basket"      to "colorful woven flower basket",
        "Cane Picnic Hamper"        to "wicker picnic basket",
        "Cane Laundry Basket"       to  "rattan basket home",
        "Bamboo Bread Basket"       to "woven bread basket",
        "Woven Cane Fruit Tray"     to "woven fruit tray",

        // LIGHTING
        "Woven Bamboo Lamp"         to "bamboo lamp shade",
        "Rattan Pendant Light"      to "rattan pendant lamp",
        "Bamboo Table Lamp"         to "bamboo table lamp",

        // DECOR
        "Bamboo Wall Art Panel"     to "bamboo wall decor",
        "Woven Cane Tray Set"       to "woven serving tray",
        "Rattan Fruit Bowl"         to "rattan fruit bowl colorful",
        "Woven Bamboo Mirror Frame" to "rattan mirror",
        "Rattan Wall Mirror"        to "round rattan mirror",
        "Bamboo Planter Stand"      to "bamboo plant stand"
    )

    // ── Category fallback queries ──────────────────────────────────────────
    private val categoryQueries = mapOf(
        "FURNITURE" to "rattan furniture",
        "BASKETS"   to "wicker basket",
        "LIGHTING"  to "rattan lamp",
        "DECOR"     to "bamboo home decor",
        "BAMBOO"    to "bamboo craft"
    )

    // ── AI design keyword map ──────────────────────────────────────────────
    // Maps product keyword → search query
    // Each query is distinct so different product types get different images
    private val aiKeywordQueries = mapOf(
        "chair"      to "  ratten chair",
        "stool"      to "bamboo stool",
        "table"      to "rattan table",
        "shelf"      to "bamboo shelf",
        "bookshelf"  to "bamboo bookshelf",
        "ladder"     to "wooden ladder shelf",
        "lamp"       to "bamboo lamp",
        "lantern"    to "bamboo lantern",
        "light"      to "rattan pendant lamp",
        "pendant"    to "rattan pendant light",
        "basket"     to "wicker basket",
        "hamper"     to "wicker hamper",
        "tray"       to "woven tray",
        "bowl"       to "rattan bowl",
        "mirror"     to "rattan mirror",
        "planter"    to "bamboo planter",
        "stand"      to "bamboo stand",
        "headboard"  to "rattan headboard",
        "trunk"      to "wicker trunk",
        "frame"      to "bamboo frame",
        "wall"       to "bamboo wall art",
        "rack"       to "bamboo rack",
        "bench"      to "bamboo bench",
        "sofa"       to "rattan sofa",
        "couch"      to "rattan couch",
        "swing"      to "rattan swing chair",
        "divider"    to "bamboo room divider",
        "mat"        to "woven bamboo mat",
        "vase"       to "bamboo vase",
        "box"        to "wicker box",
        "cabinet"    to "rattan cabinet",
        "ottoman"    to "wicker ottoman",
        "lounger"    to "rattan lounger"
    )

    // ── Style-based fallback queries ───────────────────────────────────────
    // Used when no keyword matches — style determines the search
    private val styleFallbackQueries = mapOf(
        "minimalist" to "minimalist bamboo furniture",
        "bohemian"   to "bohemian rattan decor",
        "rustic"     to "rustic wicker furniture",
        "modern"     to "modern bamboo design",
        "japanese"   to "japanese bamboo craft",
        "nordic"     to "nordic natural wood",
        "vintage"    to "vintage wicker furniture",
        "tropical"   to "tropical bamboo decor",
        "industrial" to "industrial bamboo design",
        "scandinavian" to "scandinavian natural craft"
    )

    /**
     * Get Unsplash search query for a product card.
     */
    fun getQuery(category: String, productName: String): String =
        productQueries[productName]
            ?: categoryQueries[category.uppercase().trim()]
            ?: "bamboo craft"

    /**
     * Get search query for AI design — combines product keyword + style.
     * Each card uses its own index so even same-product cards differ.
     */
    fun getAiDesignQuery(designName: String, style: String = ""): String {
        val nameLower  = designName.lowercase()
        val styleLower = style.lowercase()

        // Find matching product keyword
        val productQuery = aiKeywordQueries.entries
            .firstOrNull { nameLower.contains(it.key) }
            ?.value

        // Find matching style modifier
        val styleQuery = styleFallbackQueries.entries
            .firstOrNull { styleLower.contains(it.key) }
            ?.value

        return when {
            // Both product and style found — combine for precise query
            productQuery != null && styleQuery != null ->
                "$productQuery $styleLower"
            // Only product found
            productQuery != null ->
                productQuery
            // Only style found — use style-based query
            styleQuery != null ->
                styleQuery
            // Nothing found — use style name as search term
            style.isNotBlank() ->
                "bamboo ${style.lowercase()} craft"
            // Absolute fallback
            else ->
                "bamboo cane craft"
        }
    }
}