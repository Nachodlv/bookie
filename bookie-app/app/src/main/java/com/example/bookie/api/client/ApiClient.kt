package com.example.bookie.api.client

import android.content.Context
import com.android.volley.*
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.StringRequest
import com.example.bookie.api.ApiResponse
import com.example.bookie.api.routes.ApiRoute
import org.json.JSONObject

abstract class ApiClient(val ctx: Context?) {

    /***
     * PERFORM REQUEST
     */
    protected fun performRequest(route: ApiRoute, completion: (apiResponse: ApiResponse) -> Unit) {
        val request: StringRequest =
            object : StringRequest(route.httpMethod, route.url, { response ->
                completion(ApiResponse(200, response))
            }, {
                it.printStackTrace()
                if (it.networkResponse != null && it.networkResponse.data != null)
                    this.handle(it.networkResponse, completion)
                else
                    this.handle(it.networkResponse, completion)
            }) {

                override fun getHeaders(): MutableMap<String, String> {
                    return route.headers
                }

                @Throws(AuthFailureError::class)
                override fun getBody(): ByteArray {
                    return JSONObject(route.params as Map<String, String>).toString().toByteArray()
                }
            }
        request.retryPolicy = DefaultRetryPolicy(
            route.timeOut,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        getRequestQueue()!!.add(request)
    }

    /**
     * This method will make the creation of the answer as ApiResponse
     **/
    private fun handle(
        networkResponse: NetworkResponse,
        completion: (apiResponse: ApiResponse) -> Unit
    ) {

        val ar = ApiResponse(
            networkResponse.statusCode,
            if (networkResponse.data.isNotEmpty())
                JSONObject(networkResponse.data.toString()).toString()
            else ""
        )
        completion.invoke(ar)
    }

    /**
     * This method will return the error as String
     **/
    private fun getStringError(volleyError: VolleyError): String {
        return when (volleyError) {
            is TimeoutError -> "The conection timed out."
            is NoConnectionError -> "The conection couldn´t be established."
            is AuthFailureError -> "There was an authentication failure in your request."
            is ServerError -> "Error while prosessing the server response."
            is NetworkError -> "Network error, please verify your conection."
            is ParseError -> "Error while prosessing the server response."
            else -> "Internet error"
        }
    }

    /**
     * We create and return a new instance for the queue of Volley requests.
     **/
    private fun getRequestQueue(): RequestQueue? {
        val maxCacheSize = 20 * 1024 * 1024
        val cache = DiskBasedCache(ctx?.cacheDir, maxCacheSize)
        val netWork = BasicNetwork(HurlStack())
        val mRequestQueue = RequestQueue(cache, netWork)
        mRequestQueue.start()
        System.setProperty("http.keepAlive", "false")
        return mRequestQueue
    }

}