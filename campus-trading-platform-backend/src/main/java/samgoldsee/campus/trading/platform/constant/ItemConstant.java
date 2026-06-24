package samgoldsee.campus.trading.platform.constant;

/**
 * 物品相关常量
 *
 * @author HuangChunXin
 * @date 2026/06/22
 */
public class ItemConstant {

    /**
     * 交易类型：求购
     */
    public static final int TYPE_BUY = 1;

    /**
     * 交易类型：转让
     */
    public static final int TYPE_SELL = 2;

    /**
     * 物品状态：展示中
     */
    public static final int STATUS_ACTIVE = 0;

    /**
     * 物品状态：已成交
     */
    public static final int STATUS_SOLD = 1;

    /**
     * 物品状态：已失效
     */
    public static final int STATUS_EXPIRED = 2;

    /**
     * 物品状态：已删除
     */
    public static final int STATUS_DELETED = 3;

    /**
     * 物品过期天数（默认14天）
     */
    public static final int EXPIRE_DAYS = 14;

    /**
     * 描述最大字数
     */
    public static final int CONTENT_MAX_LENGTH = 200;

    /**
     * 每页默认大小
     */
    public static final int DEFAULT_PAGE_SIZE = 10;
}