package com.lfy.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeleteRequest implements Serializable {

    private static final long serialVersionUID = 445050928917469477L;

    private long id;
}
