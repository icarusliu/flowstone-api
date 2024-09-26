package com.liuqi.common;

import com.liuqi.base.bean.dto.ClientDTO;

/**
 * ClientHolder
 *
 * @author  LiuQi 2024/9/26-9:02
 * @version V1.0
 **/
public class ClientHolder {
    private static final ThreadLocal<ClientDTO> holder = new ThreadLocal<>();

    public static void set(ClientDTO client) {
        holder.set(client);
    }

    public static ClientDTO get() {
        return holder.get();
    }
}
