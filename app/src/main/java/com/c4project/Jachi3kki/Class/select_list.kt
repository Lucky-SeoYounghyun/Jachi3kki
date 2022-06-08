package com.c4project.Jachi3kki.Class

// 냉장고 아이템 선택시 사용되는 자료형 (RefrigeratorAdapter에서 사용됨)
class select_list() {
    var selectList = ArrayList<Long>()

    fun select_add(num: Long) {
        selectList.add(num)
    }

    fun select_remove(num: Long) {
        selectList.remove(num)
    }

    fun select_contain(num: Long) {
        selectList.contains(num)
    }

    fun select_print(num: Long) {
        println(selectList)
    }

    fun select_size() {
        println("리스트길이" + selectList.size)
    }

    fun select_print_all() {
        println("전부" + selectList)
    }

    fun click(num: Int) {
        for (i in 0 until 2) {
            if (!selectList.contains(num.toLong())) {
                selectList.add(num.toLong())
            }
        }
    }

    fun click_del() {
        selectList.clear()
    }

    fun select_check(num: Int) {
        var length = selectList.size - 1
        var check = 0

        for (i in 0..length) {
            try {
                check = 1

                if (selectList[i].compareTo(num) == 0 && check == 1) {
                    selectList.removeAt(i)
                    if (selectList[i].compareTo(num) > 0) {
                        selectList[i] = selectList[i] - 1
                        check = 2
                    }
                    check = 2
                } else if (selectList[i].compareTo(num) > 0 && check == 1) {
                    selectList[i] = selectList[i] - 1
                    check = 2
                } else if (selectList[i].compareTo(num) < 0 && check == 1) {
                    selectList[i] = selectList[i]
                    check = 2
                }
            } catch (ioe: IndexOutOfBoundsException) {
                println("index over") //인덱스 오버가됨(remove를 할경우 한정적으로)
            }
        }

    }
}