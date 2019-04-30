package com.nz.rpc.utils;

public class MathUtil {

    /**
     *功能描述
     * @author lgj
     * @Description 求取平均数
     * @date 4/28/19
     * @param:　last_avg：上一次的平均值
     * 　　　　　next_val: 本次的值
     *          num: 到目前为止次数
     *          new_avg =  ( (last_avg * (num-1)) + cur_val) / num
     * 　　　　　
     * @return:
     *
     */
    public static float calcAverage(float last_avg, float cur_val, long num)
    {
        float avg_val=0;
        if(num<=1)
        {
            avg_val=cur_val;
        }
        else
        {
            avg_val=(last_avg*(((float)num-1)/(float)num)+cur_val*(1/(float)num));/*必须强转float*/
        }
        return avg_val;
    }

}
