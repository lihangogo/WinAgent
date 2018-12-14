package com.serviceImpl;

import com.dao.FeedbackDao;
import com.daoImpl.FeedbackDaoImpl;
import com.entity.Feedback;
import com.service.FeedbackService;

import java.util.ArrayList;
import java.util.List;

public class FeedbackServiceImpl implements FeedbackService {

    @Override
    public boolean addFeedback(Feedback feedback) {
        FeedbackDao feedbackDao=new FeedbackDaoImpl();
        if(feedbackDao.addFeedback(feedback))
            return true;
        return false;
    }

    @Override
    public List<Feedback> searchFeedback() {
        FeedbackDao feedbackDao=new FeedbackDaoImpl();
        return feedbackDao.searchFeedback();
    }
}
