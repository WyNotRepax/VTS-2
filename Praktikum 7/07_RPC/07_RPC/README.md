# ONC RPC: ls als Low-level RPC

Die Client (siehe VS_07_RLS_LLP_Client) und Server (VS_07_RLS_LLP_Server) liegen als Netbeans-Projekte vor. Generierung und Starten kann aus Netbeans heraus erfolgen. 

Alternativ können diese auch per Makefile generiert 
werden:

## Erzeugung der Executables

Per `make clean; make`  (in den Verzeichnissen VS_07_RLS_LLP_Client bzw. VS_07_RLS_LLP_Server)

## Starten: 	

Ggf. zunaechst RPC-Daemon starten: 	`portmap` oder `sudo rpcbind` 

Kontrolle, ob Daemon läuft: 	`rpcinfo -p localhost`

Starten des Servers im Verzeichnis VS_07_RLS_LLP_Server: `./rls_server`

Aufruf des Clients im Verzeichnis VS_07_RLS_LLP_Client: `./rls_client <host name> <remote dir`




		
