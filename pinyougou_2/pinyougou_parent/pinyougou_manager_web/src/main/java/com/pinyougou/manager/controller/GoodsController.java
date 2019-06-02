package com.pinyougou.manager.controller;
import java.util.Arrays;
import java.util.List;

import com.alibaba.dubbo.common.json.JSON;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonJsonView;
//import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbItem;
//import com.pinyougou.search.service.SearchService;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.vo.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;

import entity.PageResult;
import entity.Result;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return goodsService.findPage(page, rows);
	}
	

	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
		try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id){
		return goodsService.findOne(id);		
	}
    @Autowired
    private Destination queueSearchDeleteDestination;
    @Autowired
	private Destination topicPageDeleteDestination;
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(final Long [] ids){
		try {
			goodsService.delete(ids);

//			searchService.deleteFromSolr(Arrays.asList(ids));
            jmsTemplate.send(queueSearchDeleteDestination, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    return session.createObjectMessage(ids);
                }
            });

            jmsTemplate.send(topicPageDeleteDestination, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    return session.createObjectMessage(ids);
                }
            });
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
		return goodsService.findPage(goods, page, rows);		
	}

//	@Reference(timeout = 100000)
//	private SearchService searchService;
	@Autowired
	private JmsTemplate jmsTemplate;

	@Autowired
	private Destination queueSearchDestination;

	@RequestMapping("/updateAuditStatus")
	public Result updateAuditStatus(Long[] ids,String statusNum) {

		List<TbItem> tbItems = goodsService.findItemByGoodId(ids, statusNum);
//		searchService.sychonizeSolr(tbItems);
		final String s = com.alibaba.fastjson.JSON.toJSONString(tbItems);
        jmsTemplate.send(queueSearchDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage(s);
            }
        });

        for (Long id : ids) {
            this.genHtml(id);
        }

		return goodsService.updateAuditStatus(ids, statusNum);
	}
//	@Reference
//	private ItemPageService itemPageService;
    @Autowired
    private Destination topicPageDestination;
	@RequestMapping("/genHtml")
	public void genHtml(Long goodsId) {
//		itemPageService.generatePage(goodsId);
        jmsTemplate.send(topicPageDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage(goodsId+"");
            }
        });
	}
	
}
