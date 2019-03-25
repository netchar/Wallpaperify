package com.netchar.wallpaperify.data.models

import okhttp3.Response
import retrofit2.HttpException

/**
 * Created by Netchar on 17.03.2019.
 * e.glushankov@gmail.com
 */

//data class Resource<out T>(val status: Status, val data: T?, val message: String?) {
//    companion object {
//        fun <T> success(data: T?): Resource<T> {
//            return Resource(Status.SUCCESS, data, null)
//        }
//
//        fun <T> error(msg: String, data: T?): Resource<T> {
//            return Resource(Status.ERROR, data, msg)
//        }
//
//        fun <T> loading(data: T?): Resource<T> {
//            return Resource(Status.LOADING, data, null)
//        }
//    }
//}
//
//enum class Status {
//    SUCCESS,
//    ERROR,
//    LOADING
//}
//
//public sealed class Result<out T : Any> {
//    /**
//     * Successful result of request without errors
//     */
//    public class Ok<out T : Any>(
//        public val value: T,
//        override val response: Response
//    ) : Result<T>(), ResponseResult {
//        override fun toString() = "Result.Ok{value=$value, response=$response}"
//    }
//
//    /**
//     * HTTP error
//     */
//    public class Error(
//        override val exception: HttpException,
//        override val response: Response
//    ) : Result<Nothing>(), ErrorResult, ResponseResult {
//        override fun toString() = "Result.Error{exception=$exception}"
//    }
//
//    /**
//     * Network exception occurred talking to the server or when an unexpected
//     * exception occurred creating the request or processing the response
//     */
//    public class Exception(
//        override val exception: Throwable
//    ) : Result<Nothing>(), ErrorResult {
//        override fun toString() = "Result.Exception{$exception}"
//    }
//
//}
//
///**
// * Interface for [Result] classes with [okhttp3.Response]: [Result.Ok] and [Result.Error]
// */
//public interface ResponseResult {
//    val response: Response
//}
//
///**
// * Interface for [Result] classes that contains [Throwable]: [Result.Error] and [Result.Exception]
// */
//public interface ErrorResult {
//    val exception: Throwable
//}