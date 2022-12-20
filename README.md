#include <linux/init.h>
#include <linux/module.h>
#include <linux/kernel.h>
#include <linux/list.h>
#include <linux/slab.h>
#include <linux/types.h>
struct birthday {
int day;
int month;
int year;
struct list head list;
};
static LIST HEAD (birthday list);
struct birthday *person01 = NULL
struct birthday *person02 = NULL
struct birthday *person03 = NULL
struct birthday *person04 = NULL
struct birthday *person05 = NULL
