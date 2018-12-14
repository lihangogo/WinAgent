package com.service;

import com.entity.Feedback;

import java.util.List;

public interface FeedbackService {

    /**
     * 添加反馈信息
     * @param feedback
     * @return
     */
    boolean addFeedback(Feedback feedback);

    /**
     * 获取所有反馈信息
     * @return
     */
    List<Feedback> searchFeedback();

}
