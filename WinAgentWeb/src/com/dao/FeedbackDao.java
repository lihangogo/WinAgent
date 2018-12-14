package com.dao;

import com.entity.Feedback;

import java.util.List;

public interface FeedbackDao {

    /**
     * 添加反馈信息
     * @param feedback
     * @return
     */
    boolean addFeedback(Feedback feedback);

    /**
     * 查看所有反馈信息
     * @return
     */
    List<Feedback> searchFeedback();

}
