ALL = rls_server
RPCGEN = rpcgen

all: $(ALL)

rls_svc.c: rls.x
	$(RPCGEN) rls.x
	$(RM) rls_clnt.c

rls_server: rls_svc_proc.c rls_svc.c rls_xdr.c rls.h
	gcc -o rls_server rls_xdr.c rls_svc_proc.c rls_svc.c

clean:
	$(RM) *.o  rls_xdr.c rls_svc.c rls.h rls_server
