secure-message
==============

Secure group based messaging service built on top of JMS

Secure Message implements a novel approach to an efficient distributed Diffie-Hellman protocol. The protocol takes advantage of the many-to-many communication scheme of group chat. For a new client to connect only 3 messages need to be sent to generate a secret shared key for all connected clients.

Secure Message is not fit for use at the moment. There is no user authentication and anyone is allows to join any group, meaning anyone can look at your messages.

