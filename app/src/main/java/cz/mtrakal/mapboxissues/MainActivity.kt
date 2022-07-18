package cz.mtrakal.mapboxissues

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.geojson.Point
import com.mapbox.search.*
import com.mapbox.search.result.SearchResult
import com.mapbox.search.result.SearchSuggestion
import timber.log.Timber
import timber.log.Timber.Forest.plant

class MainActivity : AppCompatActivity() {
    lateinit var queryEditText: EditText
    lateinit var searchResultsView: RecyclerView

    init {
        plant(Timber.DebugTree())
    }

    val accessToken by lazy { applicationContext.getString(R.string.mapbox_access_token) }
    val searchEngine by lazy {
        MapboxSearchSdk.createSearchEngine(
            SearchEngineSettings(
                accessToken,
                LocationEngineProvider.getBestLocationEngine(this)
            )
        )
    }

    private var searchRequestTask: SearchRequestTask? = null

    private val options: SearchOptions
        get() = SearchOptions.Builder().apply {
            countries(
                listOf(
                    Country.CZECHIA,
                    Country.POLAND,
                    Country.SLOVAKIA
                )
            )
            types(QueryType.ADDRESS, QueryType.POI, QueryType.PLACE, QueryType.POSTCODE)
            languages(Language("cs")) // Important, because results are different for specified countries!
            limit(10)
            // Optional, it's not set in playground, but place is close to Searched places.
            proximity(Point.fromLngLat(50.6491408, 15.2101494))
        }.build()

    private val searchCallback: SearchSelectionCallback = object : SearchSelectionCallback {
        override fun onSuggestions(suggestions: List<SearchSuggestion>, responseInfo: ResponseInfo) {
            fillSuggestions(suggestions)
        }

        override fun onCategoryResult(suggestion: SearchSuggestion, results: List<SearchResult>, responseInfo: ResponseInfo) {
            Timber.e("Category search results: $results")
        }

        override fun onError(e: Exception) {
            Timber.e(e)
        }

        override fun onResult(suggestion: SearchSuggestion, result: SearchResult, responseInfo: ResponseInfo) {
            // real result with Location after request specified Suggestion by searchEngine.select()
            Timber.e(result.toString())
        }
    }

    /**
     * Fill data to View
     */
    private fun fillSuggestions(suggestions: List<SearchSuggestion>) {
        Timber.e("_\n" + suggestions.map { it.fullAddress }.joinToString("\n"))
        searchResultsView.adapter = SuggestionAdapter(suggestions)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPermission()

        queryEditText = findViewById(R.id.query_edit_text)
        searchResultsView = findViewById<RecyclerView?>(R.id.results).apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@MainActivity)
        }

        queryEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                /* no-op */
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchRequestTask = searchEngine.search(s?.toString() ?: "", options, searchCallback)
            }

            override fun afterTextChanged(s: Editable?) {
                /* no-op */
            }
        })
    }

    private fun requestPermission() {
        if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                PERMISSIONS_REQUEST_LOCATION
            )
        }
    }

    private companion object {

        private const val PERMISSIONS_REQUEST_LOCATION = 0

        fun Context.isPermissionGranted(permission: String): Boolean {
            return ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
}
