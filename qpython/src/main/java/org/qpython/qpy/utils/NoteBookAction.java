package org.qpython.qpy.utils;

/**
 * 文 件 名: NoteBookAction
 * 创 建 人: ZhangRonghua
 * 创建日期: 2018/2/8 11:27
 * 修改时间：
 * 修改备注：
 */

public class NoteBookAction {
    //运行全部cell
    public static final String PLAY_ALL_CELL = "$('li#run_all_cells').click()";
    //清空当前输出
    public static final String CLEAR_CURRENT_OUTPUT = "$('li#clear_current_output').click()";

    //从底部添加一个cell
    public static final String ADD_CELL_BELOW = "$('li#insert_cell_below').click()";

    //切换cell为code类型
    public static final String SWITCH_CODE_TYPE = "$('select#cell_type').val('code').change()";

    //切换cell为markdown类型
    public static final String SWITCH_MARKDOWN_TYPE = "$('select#cell_type').val('markdown').change()";

    //删除一个cell
    public static final String DELETE_CELL = "$('li#delete_cell').click()";

    //取消删除cell
    public static final String UNDELETE_CELL = "$('li#undelete_cell').click()";

    //保存文档
    public static final String SAVE_NOTEBOOK = "$('button[data-jupyter-action=\"jupyter-notebook:save-notebook\"]')[0].click()";

    //undo
    public static final String CELL_UNDO = "";

    //redo
    public static final String CELL_REDO = "";

    //运行当前cell并选择下一个cell
    public static final String PLAY_CURRENT_CELL = "$('button[data-jupyter-action=\"jupyter-notebook:run-cell-and-select-next\"]')[0].click()";

    //运行全部cell
    public static final String RUN_ALL = "";

    //清空输出 & 重启kernel
    public static final String CLEAR_ALL_OUTPUT = "$('li#clear_all_output').click()";

    //shutdown
    public static final String SHUTDOWN_KERNEL = "$('li#shutdown_kernel').click()";

    //移动cell到下一个cell
    public static final String MOVE_CELL_DOWN = "$('button[data-jupyter-action=\"jupyter-notebook:move-cell-down\"]')[0].click()";

    //移动cell到上一个cell
    public static final String MOVE_CELL_UP = "$('button[data-jupyter-action=\"jupyter-notebook:move-cell-up\"]')[0].click()";

    //剪切
    public static final String CELL_CUT = "";

    //复制
    public static final String CELL_COPY = "";

    //粘贴
    public static final String CELL_PASTE = "";
}
