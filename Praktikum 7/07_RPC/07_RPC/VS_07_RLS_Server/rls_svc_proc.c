/* rls_svc_proc.c Server procedure for directory listing */
#include <rpc/rpc.h>
#include <dirent.h>
#include <errno.h>
#include "rls.h"

extern int errno;
extern void *malloc();
extern char *strdup();

readdir_res *readdir_1_svc(nametype *dirname, struct svc_req *request)
{
    namelist nl;
    namelist *nlp;

    /* Muss als static vereinbart werden, damit das Resultat
       weiter verarbeitet werden kann. */
    static readdir_res res;
    /* static, damit wir sehen koennen, ob es bereits 
       einen Aufruf gab */
    static DIR *dirp = NULL;

    struct dirent *d;
    /* Hinweis:
       struct svc_req *request
       enthaelt in req->rq_xprt->xp_raddr.sin_addr.s_addr  
       die IP-Adresse des aufrufenden Clients */

    /* Verzeichnis oeffnen  */
    dirp = opendir(*dirname);
    if( dirp == NULL ) {
        res.remoteErrno = errno;
        return(&res);
    }

    /* Speicher aus vorheriger Konvertierung freigeben */
    if( dirp ) xdr_free((xdrproc_t) xdr_readdir_res, (char *)&res);

    /* Durch Verzeichnis laufen */
    nlp = &res.readdir_res_u.list;
    while( d = readdir(dirp) ) {
        nl = *nlp = (namenode *)malloc(sizeof(namenode));
        nl->name = strdup(d->d_name);
        nlp = &nl->pNext;
    }
    *nlp = NULL;

    /* Rückgabe Ergebnis, Argument wird ueberschrieben */
    res.remoteErrno = 0;
    closedir(dirp);
    return(&res);
}
