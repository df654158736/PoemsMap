package com.poemsmap.darren.poemsmap.supercache;

import com.google.gson.stream.JsonReader;
import com.lzy.okgo.callback.AbsCallback;
import com.poemsmap.darren.poemsmap.model.GankResponse;
import com.poemsmap.darren.poemsmap.util.Convert;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Response;

/**
 * Created by Darren on 2018/3/21.
 */

public abstract class NewsCallback<T> extends AbsCallback<T> {

    /**
     * 这里的数据解析是根据 http://gank.io/api/data/Android/10/1 返回的数据来写的
     * 实际使用中,自己服务器返回的数据格式和上面网站肯定不一样,所以以下是参考代码,根据实际情况自己改写
     */
    @Override
    public T convertResponse(Response response) throws Throwable {
        //以下代码是通过泛型解析实际参数,泛型必须传
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        Type type = params[0];
        if (!(type instanceof ParameterizedType)) throw new IllegalStateException("没有填写泛型参数");

        JsonReader jsonReader = new JsonReader(response.body().charStream());
        T gankResponse = Convert.fromJson(jsonReader, type);
        response.close();
        //noinspection unchecked
        return gankResponse;


    }
}
