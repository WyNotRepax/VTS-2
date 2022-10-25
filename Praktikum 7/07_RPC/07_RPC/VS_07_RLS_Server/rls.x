/* rls.x: Definitions for remote listing protocol */
const MAXNAMELEN = 512;

/* directory entry */
typedef string nametype<MAXNAMELEN>;

/* a linked list for directory entries */
typedef struct namenode *namelist;

/* a node in the list */
struct namenode {
    nametype name; namelist pNext;
};

/* the result of the READDIR operation  */
union readdir_res switch(int remoteErrno) {
	case 0: 	namelist list;
	default:	void;
};

/* the directory program definition */
program DIRPROG {
    version DIRVERS {
        readdir_res READDIR(nametype) = 1;
    } = 1;
} = 0x20000001;
