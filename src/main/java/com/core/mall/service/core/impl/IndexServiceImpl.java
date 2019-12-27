package com.core.mall.service.core.impl;

import com.core.mall.enums.ConfigGlobalEnum;
import com.core.mall.model.entity.ConfigGlobal;
import com.core.mall.model.params.IndexMainResp;
import com.core.mall.repository.ConfigGlobalRepository;
import com.core.mall.service.core.IndexService;
import com.core.mall.util.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class IndexServiceImpl implements IndexService {
    private final static Logger logger = LoggerFactory.getLogger(IndexServiceImpl.class);

    @Autowired
    private ConfigGlobalRepository configGlobalRepository;

    @Override
    public IndexMainResp indexMain() {
        IndexMainResp response = new IndexMainResp();

        response.setName(configGlobalRepository.findValueByKey(ConfigGlobalEnum.CONFIG_INDEX_SHOP_NAME.getValue()));
        response.setImage(configGlobalRepository.findValueByKey(ConfigGlobalEnum.CONFIG_INDEX_SHOP_IMG_URL.getValue()));

        // 查询商家信息
        List<ConfigGlobal> infoList = configGlobalRepository.findByGlobalKeyLike(ConfigGlobalEnum.CONFIG_INDEX_SHOP_INFO.getValue());
        // 查询后厨信息
        List<ConfigGlobal> picList = configGlobalRepository.findByGlobalKeyLike(ConfigGlobalEnum.CONFIG_INDEX_SHOP_PIC.getValue());
        // 查询活动信息
        List<ConfigGlobal> supportList = configGlobalRepository.findByGlobalKeyLike(ConfigGlobalEnum.CONFIG_INDEX_SHOP_SUPPORT.getValue());

        if (infoList != null && infoList.size() > 0) {
            List<String> info = new ArrayList<>();
            for (ConfigGlobal configGlobal : infoList) {
                info.add(configGlobal.getGlobalValue());
            }
            response.setInfo(info);
        }

        if (picList != null && picList.size() > 0) {
            List<String> pics = new ArrayList<>();
            for (ConfigGlobal configGlobal : picList) {
                pics.add(configGlobal.getGlobalValue());
            }
            response.setPic(pics);
        }

        if (supportList != null && supportList.size() > 0) {
            List<IndexMainResp.Support> supports = new ArrayList<>();
            for (ConfigGlobal configGlobal : supportList) {
                String[] str = configGlobal.getGlobalValue().split(",");
                IndexMainResp.Support support = new IndexMainResp.Support();
                support.setName(str[1]);
                support.setType(Integer.parseInt(str[0]));

                supports.add(support);
            }
            response.setSupports(supports);
        }

        String halt = configGlobalRepository.findValueByKey(ConfigGlobalEnum.CONFIG_INDEX_BUSINESS_SHOP_HALT.getValue());
        if ("0".equals(halt)) {
            int currentTimeStamp = Utility.getCurrentTimeStamp();
            int startTime = Utility.getDayStartTime(currentTimeStamp) + Integer.parseInt(configGlobalRepository.findValueByKey(ConfigGlobalEnum.CONFIG_INDEX_BUSINESS_START_TIME.getValue()));
            int endTime = Utility.getDayStartTime(currentTimeStamp) + Integer.parseInt(configGlobalRepository.findValueByKey(ConfigGlobalEnum.CONFIG_INDEX_BUSINESS_END_TIME.getValue()));
            if (!(startTime <= currentTimeStamp && currentTimeStamp <= endTime)) {
                halt = "1";
            }
        }

        response.setHalt(halt);
        response.setBulletin(configGlobalRepository.findValueByKey(ConfigGlobalEnum.CONFIG_INDEX_SHOP_DESC.getValue()));
        response.setDescription(configGlobalRepository.findValueByKey(ConfigGlobalEnum.CONFIG_INDEX_SHOP_DESCRIPTION.getValue()));
        logger.info("indexMain response={}", response);
        return response;
    }
}
