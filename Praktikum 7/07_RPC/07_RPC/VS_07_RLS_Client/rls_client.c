/*
 * rls.c - remote directory-listing client
 * Quelle Bloomer, Power RPC Programming
 * Adaptiert von C. Westerkamp WS2009/10	
 */
#include <stdio.h>
#include <rpc/rpc.h>
#include <errno.h>
#include "rls.h"

extern int errno;

int main(int argc, char *argv[])
{
    CLIENT *cl;
    char *server;
    char *dir;
    readdir_res *result;
    namelist nl;

    if( argc != 3 ) {
        fprintf(stderr, "usage: %s host directory\n", argv[0]);
        exit(1);
    }

    /* Command line arguments */
    server = argv[1];
    dir = argv[2];

    /* Client handle erzeugen (mit TCP Protokoll) */
    cl = clnt_create(server, DIRPROG, DIRVERS, "tcp");
    if( cl == NULL ) {
        /* Verbindungsaufbau erfolglos  */
        clnt_pcreateerror(server);
        exit(1);
    }
    /* Eigentlicher Aufruf der entfernten Prozedur */ 
    result = readdir_1(&dir, cl);
    if( result == NULL ) {
        /* Fehler beim RPC-Aufruf */
        clnt_perror(cl, server);
        exit(1);
    }
    /* RPC-Aufruf erfolgreich, Resultat auswerten */
    if( result->remoteErrno != 0 ) { 
        /* RPC-Fehler aus Server */
        errno = result->remoteErrno;
        perror(dir);
        exit(1);
    }
    /* Resultat ohne Fehler, Verzeichnisliste ausgeben */
    for( nl=result->readdir_res_u.list;
        nl != NULL;
        nl = nl->pNext) {
        
        printf("%s\n", nl->name);
    }
    exit(0);
}


