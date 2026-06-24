package samgoldsee.campus.trading.platform.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import samgoldsee.campus.trading.platform.mapper.ItemMapper;

/**
 * 物品定时任务
 * 自动过期物品下架
 *
 * @author HuangChunXin
 * @date 2026/06/22
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ItemExpireTask {

    private final ItemMapper itemMapper;

    /**
     * 每小时执行一次，自动下架过期物品
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void expireItems() {
        try {
            int count = itemMapper.expireItems();
            if (count > 0) {
                log.info("自动下架过期物品: {} 条", count);
            }
        } catch (Exception e) {
            log.error("自动下架过期物品失败", e);
        }
    }
}