package com.core.mall.model.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@ToString(callSuper = true)
@Data
@Entity
public class UtilVendorAsyncTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private long createTime;
    @Column(nullable = false)
    private long updateTime;
    @Column(nullable = false)
    private Short task;
    @Column()
    private Integer status;
    @Column()
    private String orderId;
    @Column()
    private String context;
    @Column()
    private Integer nextTime;
    @Column()
    private Integer retryNum;

    public final static int STATUS_UNKNOWN = 0;
    public final static int STATUS_SUCCESS = 1;
    public final static int STATUS_FAIL = 2;
    public final static int STATUS_SERVICE_UNAVAILABLE = 3;
    public final static int STATUS_BAD_REQUEST = 4;
    public final static int STATUS_EXCEED_MAX_TIMEOUT = 5;

    // tasks
    public final static int TASK_WE_CHAT = 1;
    public final static int TASK_ORDER_STATUS = 2;

    public int scheduleNextRetry() {
        if (retryNum < 5) {
            nextTime = (int) (System.currentTimeMillis() / 1000 + 10);
        } else if (retryNum < 8) {
            nextTime = (int) (System.currentTimeMillis() / 1000 + 60 * Math.pow(2.0, retryNum - 5));
        } else {
            nextTime = (int) (System.currentTimeMillis() / 1000 + 60 * 5);
        }
        retryNum += 1;
        return nextTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UtilVendorAsyncTask that = (UtilVendorAsyncTask) o;

        if (!(id == that.id)) return false;
        if (!status.equals(that.status)) return false;
        if (!task.equals(that.task)) return false;
        if (context != null ? !context.equals(that.context) : that.context != null) return false;
        if (!orderId.equals(that.orderId)) return false;
        if (!retryNum.equals(that.retryNum)) return false;
        if (!(createTime == that.createTime)) return false;
        if (!(updateTime == that.updateTime)) return false;
        return nextTime.equals(that.nextTime);
    }

    @Override
    public int hashCode() {
        long result = id;
        result = 31 * result + status;
        result = 31 * result + task.hashCode();
        result = 31 * result + (context != null ? context.hashCode() : 0);
        result = 31 * result + orderId.hashCode();
        result = 31 * result + retryNum.hashCode();
        result = 31 * result + nextTime.hashCode();
        return (int) result;
    }
}