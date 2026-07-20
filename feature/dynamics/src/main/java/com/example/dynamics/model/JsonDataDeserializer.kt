package com.example.dynamics.model

import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type

/**
 * 自定义反序列化器：API 返回的 json 字段是一个 JSON 字符串（"{\"msg\":...}"），
 * 但 Event 类中声明为 JsonData 对象。Gson 默认无法自动把字符串解析成嵌套对象。
 * 这个反序列化器判断当前值：是字符串 → 先取字符串值再解析为 JsonData；
 * 是对象 → 直接解析。
 */
class JsonDataDeserializer : JsonDeserializer<JsonData> {
    private val gson = Gson()

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): JsonData {
        return when {
            json.isJsonPrimitive && json.asJsonPrimitive.isString -> {
                gson.fromJson(json.asString, JsonData::class.java)
            }
            json.isJsonObject -> {
                gson.fromJson(json, JsonData::class.java)
            }
            else -> throw JsonParseException("Unexpected type for json field: $json")
        }
    }
}
