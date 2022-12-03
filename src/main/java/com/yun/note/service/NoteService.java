package com.yun.note.service;

import cn.hutool.core.util.StrUtil;
import com.yun.note.dao.NoteDao;
import com.yun.note.po.Note;
import com.yun.note.po.User;
import com.yun.note.util.Page;
import com.yun.note.vo.NoteVo;
import com.yun.note.vo.ResultInfo;

import java.util.List;
import java.util.Map;

public class NoteService {

    private NoteDao noteDao = new NoteDao();


    /**
       添加或修改云记
               1. 参数的非空判断
                  如果为空，code=0，msg=xxx，result=note对象，返回resultInfo对象
               2. 设置回显对象 Note对象
               3. 调用Dao层，添加云记记录，返回受影响的行数
               4. 判断受影响的行数
                   如果大于0，code=1
                   如果不大于0，code=0，msg=xxx，result=note对象
               5. 返回resultInfo对象
     * @param typeId
     * @param title
     * @param content
     * @return
     */
    public ResultInfo<Note> addOrUpdate(String typeId, String title, String content,String noteId) {
           ResultInfo<Note> resultInfo = new ResultInfo<>();
           //1. 参数的非空判断
           if (StrUtil.isBlank(typeId)){
               resultInfo.setCode(0);
               resultInfo.setMsg("类型不能为空");
               return resultInfo;
           }
           if (StrUtil.isBlank(title)){
                resultInfo.setCode(0);
                resultInfo.setMsg("标题不能为空");
                return resultInfo;
           }
            if (StrUtil.isBlank(content)){
                resultInfo.setCode(0);
                resultInfo.setMsg("内容不能为空");
                return resultInfo;
            }
           // 2. 设置回显对象 Note对象
            Note note = new Note();
            note.setTypeId(Integer.parseInt(typeId));
            note.setTitle(title);
            note.setContent(content);
            //判断云记ID是否为空
            if (!StrUtil.isBlank(noteId)){
                note.setNoteId(Integer.parseInt(noteId));
            }
            resultInfo.setResult(note);

           //3. 调用Dao层，添加云记记录，返回受影响的行数
           int row = noteDao.addOrUpdate(note);
           //4. 判断受影响的行数
            if (row > 0){
                resultInfo.setCode(1);
            }else {
                // 如果不大于0，code=0，msg=xxx，result=note对象
                resultInfo.setCode(0);
                resultInfo.setResult(note);
                resultInfo.setMsg("更新失败");
            }

            // 5. 返回resultInfo对象
           return resultInfo;
    }



    /**
      分页查询云记列表
               1. 参数的非空校验
                  如果分页参数为空，则设置默认值
               2. 查询当前登录用户的云记数量，返回总记录数 （long类型）
               3. 判断总记录数是否大于0
               4. 如果总记录数大于0，调用Page类的带参构造，得到其他分页参数的值，返回Page对象
               5. 查询当前登录用户下当前页的数据列表，返回note集合
               6. 将note集合设置到page对象中
               7. 返回Page对象
     * @param pageNumStr
     * @param pageSizeStr
     * @return
     */
    public Page<Note> findNoteListByPage(String pageNumStr, String pageSizeStr, Integer userId,String title,String date,String typeId) {
        // 设置分页参数的默认值
        Integer pageNum = 1; // 默认当前页是第一页
        Integer pageSize = 5; // 默认每页显示5条数据
        // 1. 参数的非空校验 （如果参数不为空，则设置参数为默认值）
        if (!StrUtil.isBlank(pageNumStr)) {
            // 设置当前页
            pageNum = Integer.parseInt(pageNumStr);
        }
        if (!StrUtil.isBlank(pageSizeStr)) {
            // 设置每页显示的数量
            pageSize = Integer.parseInt(pageSizeStr);
        }
        //2. 查询当前登录用户的云记数量，返回总记录数 （long类型）
        //, title, date, typeId
        Long count = noteDao.findNoteCount(userId,title,date,typeId);

        // 3. 判断总记录数是否大于0
        if (count < 1){
          return null;
        }
        // 4. 如果总记录数大于0，调用Page类的带参构造，得到其他分页参数的值，返回Page对象
        Page<Note> page = new Page(pageNum,pageSize,count);
        //得到数据库中分布查询的开始下标
        Integer index = (pageNum-1) * pageSize;
        // 5. 查询当前登录用户下当前页的数据列表，返回note集合
        //, title, date, typeId
        List<Note> noteList = noteDao.findNoteListByPage(userId, index, pageSize,title,date,typeId);
        // 6. 将note集合设置到page对象中
        page.setDataList(noteList);
        // 7. 返回Page对象
        return page;
    }

    /**
     * 通过月份查询对应的云记数量
     * @param userId
     * @return
     */
    public ResultInfo<Map<String, Object>> queryNoteContByMonth(Integer userId) {
        ResultInfo<Map<String, Object>> resultInfo = new ResultInfo<>();

         return resultInfo;
    }

    /**
     * 通过日期分组，查询当前登陆用户的云记数量
     * @param userId
     * @return
     */
    public List<NoteVo> findNoteCountByDate(Integer userId) {
        return noteDao.findNoteCountByDate(userId);
    }

    /**
     * // 通过类型分组查询当前登录用户下的云记数量
     * @param userId
     * @return
     */
    public List<NoteVo> findNoteCountByType(Integer userId) {
        return noteDao.findNoteCountByType(userId);
    }

    /**
     * 查看云记详情
     *    1. 参数的非空判断
     *    2. 调用Dao层的查询，通过noteId查询note对象
     *    3. 返回note对象
     * @param noteId
     * @return
     */
    public Note findNoteById(String noteId) {
        // 1. 参数的非空判断
        if (StrUtil.isBlank(noteId)){
            return null;
        }
        // 2. 调用Dao层的查询，通过noteId查询note对象
        Note note = noteDao.findNoteById(noteId);
        // 3. 返回note对象
        return note;
    }

    /**
     * 删除云日记
     *   1. 判断参数
     *   2. 调用Dao层的更新方法，返回受影响的行数
     *   3. 判断受影响的行数是否大于0
     *      如果大于0，返回1；否则返回0
     * @param noteId
     * @return
     */
    public Integer deleteNoteById(String noteId) {
        if (StrUtil.isBlank(noteId)){
            return null;
        }
        int row = noteDao.deleteNoteById(noteId);
        if (row > 0){
            return 1;
        }else {
            return 0;
        }
    }
}
