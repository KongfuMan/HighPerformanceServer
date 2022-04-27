import java.io.IOException;
import java.nio.channels.*;
import java.util.List;
import java.util.Set;

public class MainSelector {
    private Selector selector;

    public MainSelector() throws IOException {
        this.selector = Selector.open();
    }

    public void register(SelectableChannel channel, int ops) throws ClosedChannelException {
        channel.register(selector, ops);
    }

    /**
     * Select the channels ready for registered operations.
     * This method will block if NO channel is ready
     *
     * */
    public Set<SelectionKey> select() throws IOException {
        int selectCnt = selector.select();
        Set<SelectionKey> selectionKeys = selector.selectedKeys();
        return selectionKeys;
    }


}
