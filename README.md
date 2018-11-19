# client-server-chatapp
An implement of client server protocol in Java.
The server allow clients to...
 * create chat-rooms
 * list all existing rooms
 * join an existing chat-room
 * leave a chat-room

Chat-rooms store all data (messages sent to the room) for as long as the room exist. 

The client provides an interface that enables a user to
* create a room,
* list the existing rooms,
* join chat-rooms
* send messages to chat-rooms
* leave chat-rooms

If the user connects to a chat-room all previously sent messages of that room would be displayed. New messages sent by the user or other connected users are also been displayed.

Once created, chat-rooms store all data (messages sent to the room) for as long as they exist. If a chat-room has not been used for more than 7 days, it will be destroyed. 
