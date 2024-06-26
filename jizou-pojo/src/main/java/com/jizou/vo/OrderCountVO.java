package com.jizou.vo;

import lombok.Data;
import java.io.Serializable;

@Data
public class OrderCountVO implements Serializable {
    //待接单数量
    private Integer toBeConfirmed;

    //待派送数量
    private Integer confirmed;

    //派送中数量
    private Integer deliveryInProgress;
}
