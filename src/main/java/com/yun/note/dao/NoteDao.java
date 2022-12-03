package com.yun.note.dao;

import cn.hutool.core.util.StrUtil;
import com.yun.note.po.Note;
import com.yun.note.util.DBUtil;
import com.yun.note.vo.NoteVo;

import java.util.ArrayList;
import java.util.List;

public class NoteDao {

    /**
     * 添加或修改云记，返回受影响的行数
     * @param note
     * @return
     */
    public int addOrUpdate(Note note) {
        //定义sql语句
        String sql = "";
        //设置参数
        List<Object> params = new ArrayList<>();
        params.add(note.getTypeId());
        params.add(note.getTitle());
        params.add(note.getContent());

        //判断noteId是否为空，如果为空则为添加操作，如果不为空则为修改操作
        if (note.getNoteId() == null){
            sql = "insert into tb_note (typeId,title,content,pubTime) values (?,?,?,now())";
        }else {
            sql = "update tb_note set typeId = ?,title = ?,content = ? where noteId = ?";
                    params.add(note.getNoteId());
        }

        //调用BaseDao 方法
        int row = BaseDao.executeUpdate(sql,params);
        return row;

    }

    /**
     * 查询当前登录用户的云记数量，返回总记录数
     * @param userId
     * @return
     */
        public Long findNoteCount(Integer userId,String title,String date,String typeId) {
            // 定义sql语句(需要联表查询)
            String sql = "SELECT count(1) FROM tb_note n INNER JOIN " +
                    " tb_note_type t on n.typeId = t.typeId " +
                    " WHERE userId = ?";
            //设置参数集合
            List<Object> params = new ArrayList<>();
            params.add(userId);
            //判断条件查询的参数是否为空（如果查询参数不为空，则拼接sql语句，并设置所需要的参数）
            if (!StrUtil.isBlank(title)){//标题查询
                sql += " and title like concat('%',?,'%') ";//错误拉接写法"and title like %title%
                //设置sql所需要的参数
                params.add(title);
            }else if (!StrUtil.isBlank(date)){//日期查询
                sql += " and date_format(pubTime,'%Y年%m月') = ? ";//先将日期格式化，数据库中的是到秒的，页面里获取的时间只有年月日，先转同等格式再比
                //设置sql所需要的参数
                params.add(date);
            }else if (!StrUtil.isBlank(typeId)){//类型查询
                sql += " and n.typeId = ? ";
                //设置sql所需要的参数
                params.add(typeId);
            }


            //调用BaseDao方法，执行sql语句
            long count  = (long) BaseDao.findSingleValue(sql, params);
            return count;



        }

    /**
     * 分页查询当前登录用户下当前页的数据列表，返回note集合
     * @param userId
     * @param index
     * @param pageSize
     * @return
     */
        public List<Note> findNoteListByPage(Integer userId, Integer index, Integer pageSize,String title,String date,String typeId) {
            // 定义SQL语句
           /*  原sql语句  因为要再拼接sql语句，而limit要放在sql语句最后面，所以先拼接其他的，再拼接limit;
            String sql = "SELECT noteId,title,pubTime FROM tb_note n INNER JOIN " +
                    "tb_note_type t on n.typeId = t.typeId WHERE userId = ? limit ?,?";
           */

            String sql = "SELECT noteId,title,pubTime FROM tb_note n INNER JOIN " +
                    "tb_note_type t on n.typeId = t.typeId WHERE userId = ? ";

            // 设置参数
            List<Object> params = new ArrayList<>();
            params.add(userId);
            //判断条件查询的参数是否为空（如果查询参数不为空，则拼接sql语句，并设置所需要的参数）
            if (!StrUtil.isBlank(title)){
                sql += " and title like concat('%',?,'%')";//错误拉接写法"and title like %title%
                //设置sql所需要的参数
                params.add(title);
            }else if (!StrUtil.isBlank(date)){//日期查询
                sql += " and date_format(pubTime,'%Y年%m月') = ? ";//先将日期格式化，数据库中的是到秒的，页面里获取的时间只有年月日，先转同等格式再比
                //设置sql所需要的参数
                params.add(date);
            }else if (!StrUtil.isBlank(typeId)){//类型查询
                sql += " and n.typeId = ? ";
                //设置sql所需要的参数
                params.add(typeId);
            }

            // 拼接分页的sql语句（因为limit要写在sql的最后）
            sql += " order by pubTime desc limit ?,?";
            params.add(index);
            params.add(pageSize);

            // 调用BaseDao的查询方法
            List<Note> noteList = BaseDao.queryRows(sql, params, Note.class);

            return noteList;


        }

    /**
     * 通过日期分组，查询当前登陆用户的云记数量
     * @param userId
     * @return
     */
    public List<NoteVo> findNoteCountByDate(Integer userId) {
        // 定义SQL语句
        String sql = "SELECT count(1) noteCount,DATE_FORMAT(pubTime,'%Y年%m月') groupName FROM tb_note n " +
                " INNER JOIN tb_note_type t ON n.typeId = t.typeId WHERE userId = ? " +
                " GROUP BY DATE_FORMAT(pubTime,'%Y年%m月')" +
                " ORDER BY DATE_FORMAT(pubTime,'%Y年%m月') DESC ";

        // 设置参数
        List<Object> params = new ArrayList<>();
        params.add(userId);

        // 调用BaseDao的查询方法
        List<NoteVo> list = BaseDao.queryRows(sql, params, NoteVo.class);

        return list;
    }

    /**
     * 通过类型分组，查询当前登陆用户的云记数量
     * @param userId
     * @return
     */
    public List<NoteVo> findNoteCountByType(Integer userId) {
        // 定义SQL语句
        String sql = "SELECT count(noteId) noteCount, t.typeId, typeName groupName FROM tb_note n " +
                " RIGHT JOIN tb_note_type t ON n.typeId = t.typeId WHERE userId = ? " +
                " GROUP BY t.typeId ORDER BY COUNT(noteId) DESC ";

        // 设置参数
        List<Object> params = new ArrayList<>();
        params.add(userId);

        // 调用BaseDao的查询方法
        List<NoteVo> list = BaseDao.queryRows(sql, params, NoteVo.class);

        return list;

    }

    /**
     * 查看云记详情
     * @param noteId
     * @return
     */
    public Note findNoteById(String noteId) {
        //定义sql语句
        String sql = "select noteId,title,content,pubTime,typeName,n.typeId from tb_note n inner join tb_note_type t on n.typeId = t.typeId where noteId = ?";
        //设置参数
        List<Object> params = new ArrayList<>();
        params.add(noteId);
        //调用BaseDao的方法
        Note note = (Note) BaseDao.queryRow(sql,params,Note.class);
        return note;
    }

    /**
     * 删除云日记
     *   通过noteId删除云记记录，返回受影响的行数
     * @param noteId
     * @return
     */
    public int deleteNoteById(String noteId) {
        //定义sql语句
        String sql = "delete from tb_note where noteId = ?";
        //设置参数
        List<Object> params = new ArrayList<>();
        params.add(noteId);
        int row = BaseDao.executeUpdate(sql,params);
        return row;
    }
}
