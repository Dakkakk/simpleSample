


import org.mozilla.javascript.Context
import org.mozilla.javascript.Function

fun callJs(
    source:String,
    sourceName:String="newJs-${System.currentTimeMillis()}.js",
    functionName:String,
    vararg args:Any
):Any?{
    val context= Context.enter()
    val result: Any?
    context.optimizationLevel=-1
    try {
        val scope=context.initStandardObjects()
        context.evaluateString(scope,source,sourceName,1,null)
        val func=scope.get(functionName,scope) as Function
        result= func.call(context,scope,scope,args)
    }finally {
        Context.exit()
    }
    return result
}
fun encodeURIComponent(value: String): String {
    return callJs("", "encodeURIComponent.js", "encodeURIComponent", value) as String
}

fun decodeURIComponent(value: String): String {
    return callJs("", "decodeURIComponent.js", "decodeURIComponent", value) as String
}
