package com.afscope.ipcamera.common;

/**
 * Created by Administrator on 2018/5/11 0011.
 */
public interface CommonCallback extends Callback<CommonCallback.Result> {
    @Override
    void onResult(Result result);

    class Result {
        public boolean result;
        public String msg;
        public Result(boolean result){
            this.result = result;
        }
        public Result(boolean result, String msg){
            this.result = result;
            this.msg = msg;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder("result: ")
                    .append(result)
                    .append(", msg: ")
                    .append(msg);
            return builder.toString();
        }
    }
}
