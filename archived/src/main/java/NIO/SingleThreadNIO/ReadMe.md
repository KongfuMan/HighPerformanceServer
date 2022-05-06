### Four SelectionKey events:
- `OP_CONNECT`: client端使用，表示一个client socket channel处于acceptable状态;
- `OP_READ`: server端使用，表示(client)SocketChannel is ready to read data from client.
- `OP_WRITE`: server端使用，表示(client)SocketChannel is ready to write data into client.
- `OP_ACCEPT` server端使用，SeverSocketChannel. 表示there is client for the new connection.
