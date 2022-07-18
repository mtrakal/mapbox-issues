package cz.mtrakal.mapboxissues

import com.mapbox.search.result.SearchAddress
import com.mapbox.search.result.SearchSuggestion

/**
 * Must use this formatter for Mapbox, because it return wrong data for standard formatters for European address format (CZ/SK/PL):
 * Expected format is: "street houseNumber(without comma), city, (region,) postalCode"
 * Returned format from predefined formatters: "houseNumber, street, city, postalCode".
 * SearchAddress.FormatStyle.Full / SearchAddress.FormatStyle.Long are useless for us + return wrong results for POI (missing place name).
 *
 * SearchAddress.FormatStyle.Custom() is not possible to use, because all fields are separated by comma, which is not acceptable for CZ/SK addresses.
 */
private val SearchAddress.toAddressFormat: String
    get() = listOfNotNull(
        "${street.orEmpty()} ${houseNumber.orEmpty()}",
        place,
        region,
        postcode
    ).filter { it.isNotBlank() }.joinToString(", ")

/**
 * FIXME: 18.07.2022 Use standard formatters, not custom ones.
 */
val SearchSuggestion.fullAddress: String
    get() = address?.formattedAddress(SearchAddress.FormatStyle.Full) ?: "MISSING ADDRESS $name!" // address?.toAddressFormat ?: "MISSING ADDRESS $name!"
