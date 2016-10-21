package cn.standardai.api.core.util;

import java.util.ArrayList;
import java.util.List;

import cn.standardai.api.core.bean.SortCond;

public class SortCondParser {

    //排序项目分隔符
    private static final String COL_SPLITTER = "\\|";

    //顺序类型分隔符
    private static final String ORDER_SPLITTER = ":";

    /**
     * 将字符串转换为SortCond
     * 字符串的标准格式为
     * title:ASC|created:DESC
     *
     * @param parseString 待转换字符串
     * @return
     */
    public List<SortCond> parseList(String parseString) {
        List<SortCond> sortCondList = new ArrayList<SortCond>();
        String column = null;
        String order = null;

        for (String parse : parseString.split(COL_SPLITTER)) {
        	String[] sortCond = parse.split(ORDER_SPLITTER);
        	column = sortCond[0];
        	order = sortCond[1];
        	
            // 验证order合法性
            if (order != null && !order.equals("")) {
                Enum.valueOf(SortCond.Order.class, order);
            } else {
                order = SortCond.Order.DESC.name();
            }

            sortCondList.add(new SortCond(column, SortCond.Order.valueOf(order)));
        }

        return sortCondList;
    }
}
