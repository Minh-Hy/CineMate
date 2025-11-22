package com.yourname.cinemate.utils;

/**
 * Một lớp Wrapper (lớp vỏ) dùng cho LiveData để biểu diễn các sự kiện.
 * Một sự kiện chỉ nên được tiêu thụ (consumed) một lần duy nhất (ví dụ: hiển thị một Toast).
 * @param <T> Kiểu dữ liệu của nội dung sự kiện.
 */
public class Event<T> {

    private final T content;
    private boolean hasBeenHandled = false;

    /**
     * Tạo một Event mới.
     * @param content Nội dung của sự kiện.
     */
    public Event(T content) {
        this.content = content;
    }

    /**
     * Trả về nội dung và đánh dấu sự kiện là đã được xử lý.
     * Nếu sự kiện đã được xử lý trước đó, trả về null.
     * @return Nội dung, hoặc null nếu đã được xử lý.
     */
    public T getContentIfNotHandled() {
        if (hasBeenHandled) {
            return null;
        } else {
            hasBeenHandled = true;
            return content;
        }
    }

    /**
     * Trả về nội dung, ngay cả khi nó đã được xử lý.
     * Hữu ích để xem trước giá trị mà không tiêu thụ nó.
     * @return Nội dung của sự kiện.
     */
    public T peekContent() {
        return content;
    }

    /**
     * Kiểm tra xem sự kiện đã được xử lý chưa.
     * @return true nếu đã được xử lý, ngược lại là false.
     */
    public boolean hasBeenHandled() {
        return hasBeenHandled;
    }
}