/* 
 * Developed by R. E. Bryant, 2017
 * Extended to store strings, 2018
 */

/*
 * This program implements a queue supporting both FIFO and LIFO
 * operations.
 *
 * It uses a singly-linked list to represent the set of queue elements
 */

#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "harness.h"
#include "queue.h"

/*
  Create empty queue.
  Return NULL if could not allocate space.
*/
queue_t *q_new()
{
    queue_t *q =  malloc(sizeof(queue_t));
    /* What if malloc returned NULL? */
	if(q == NULL) return NULL;
    q->head = NULL;
	q->size = 0;
	q->tail = NULL;
    return q;
}

/* Free all storage used by queue */
void q_free(queue_t *q)
{
	if(q == NULL) return;
	list_ele_t *curr = q->head;
	list_ele_t *prevQ;
	while(curr !=NULL){
		prevQ = curr;
		free(curr->value);
		curr = curr->next;
		free(prevQ);
	}
    /* How about freeing the list elements and the strings? */
    /* Free queue structure */
    free(q);
}

/*
  Attempt to insert element at head of queue.
  Return true if successful.
  Return false if q is NULL or could not allocate space.
  Argument s points to the string to be stored.
  The function must explicitly allocate space and copy the string into it.
 */
bool q_insert_head(queue_t *q, char *s)
{
    list_ele_t *newh;
	if(q == NULL) return false;
    /* What should you do if the q is NULL? */
    newh = malloc(sizeof(list_ele_t));
	if(newh == NULL) return false;
	char *newEle = malloc(strlen(s)+1);
	if(newEle == NULL){
		free(newh);
		return false;
	}
    /* Don't forget to allocate space for the string and copy it */
    /* What if either call to malloc returns NULL? */
    newh->next = q->head;
    q->head = newh;
    newh->value = newEle;
	strcpy(newh->value, s);
	q->size++;
	if(q->size == 1) q->tail = newh;
    return true;
}


/*
  Attempt to insert element at tail of queue.
  Return true if successful.
  Return false if q is NULL or could not allocate space.
  Argument s points to the string to be stored.
  The function must explicitly allocate space and copy the string into it.
 */
bool q_insert_tail(queue_t *q, char *s)
{
    /* You need to write the complete code for this function */
    /* Remember: It should operate in O(1) time */
	list_ele_t *newt;
	if(q == NULL) return false;
    /* What should you do if the q is NULL? */
   	newt = malloc(sizeof(list_ele_t));
	if(newt == NULL) return false;
	char *newEle = malloc(strlen(s)+1);
	newt->value = newEle;
	if(newEle == NULL){
		free(newt);
		return false;
	}
	if(newt -> value == NULL){
		free(newt);
		return false;
	}
    /* Don't forget to allocate space for the string and copy it */
    /* What if either call to malloc returns NULL? */
	strcpy(newt->value,s);
	q->size++;
	if (q->tail == NULL) {
		q->head = newt;
   	 } else {
       		q->tail->next = newt; 
    	}
   	 q->tail = newt;
    	newt->next = NULL;
    	return true;
}

/*
  Attempt to remove element from head of queue.
  Return true if successful.
  Return false if queue is NULL or empty.
  If sp is non-NULL and an element is removed, copy the removed string to *sp
  (up to a maximum of bufsize-1 characters, plus a null terminator.)
  The space used by the list element and the string should be freed.
*/
bool q_remove_head(queue_t *q, char *sp, size_t bufsize)
{
    /* You need to fix up this code. */
	if(q == NULL) return false;
	if(q->head == NULL) return false;
	if(sp != NULL){
		strncpy(sp, q->head->value, bufsize - 1);
		sp[bufsize - 1] = '\0';
	}
	q->size--;
	list_ele_t *prevHead = q->head;
    q->head = q->head->next;
	free(prevHead->value);
	free(prevHead);
    return true;
}

/*
  Return number of elements in queue.
  Return 0 if q is NULL or empty
 */
int q_size(queue_t *q)
{
	if(q == NULL) return 0;
    /* You need to write the code for this function */
    /* Remember: It should operate in O(1) time */
    return q->size;
}

/*
  Reverse elements in queue
  No effect if q is NULL or empty
  This function should not allocate or free any list elements
  (e.g., by calling q_insert_head, q_insert_tail, or q_remove_head).
  It should rearrange the existing ones.
 */
void q_reverse(queue_t *q)
{
	if(q == NULL || q->size == 0 || q->size == 1) return;
	list_ele_t *prev = q->head;
	list_ele_t *curr = prev->next;
	list_ele_t *next = curr->next;
	while(curr != NULL){
		curr->next = prev;
		prev = curr;
		curr = next;
		if(next != NULL) next = next->next;
	}
	q->tail = q->head;
	q->head = prev;
	q->tail->next = NULL;
	
    /* You need to write the code for this function */
}

