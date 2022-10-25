ALL = rls_client
RPCGEN = rpcgen

all: $(ALL)

rls_clnt.c: rls.x
	$(RPCGEN) rls.x
	$(RM) rls_svc.c

rls_client: rls_client.c rls_clnt.c rls_xdr.c rls.h
	gcc -o rls_client rls_xdr.c rls_clnt.c rls_client.c

clean:
	$(RM) *.o  rls_xdr.c rls_clnt.c rls.h rls_client
