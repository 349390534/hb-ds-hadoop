package com.howbuy.rdb.database.dto;

import com.howbuy.rdb.database.dto.impl.BaseDtoImpl;

public class PageDto<T extends BaseDtoImpl> extends BaseListDto {

    private static final long serialVersionUID = -2762689922407395921L;

    private T dto;

    public void setDto(T dto) {
        this.dto = dto;
    }

    public T getDto() {
        return dto;
    }
}
