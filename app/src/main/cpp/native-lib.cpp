#include <jni.h>
#include <stdlib.h>


typedef struct List_Item{
    struct List_Item * next;
    double time;
    double distance;
    char foot;

}ListItem;

typedef struct  {
    ListItem * start;
    ListItem * top;
    int total;
}List;


void appendList(List * lista, ListItem * data){
    lista->total +=1;

    if(lista->start == NULL){
        lista -> start = data;
        lista -> top = data;
        return;
    }

    lista->top->next = data;
    lista->top = data;


}


extern "C" {
JNIEXPORT jlong JNICALL Java_com_avant_joao_avant_MainActivity_startList(
        JNIEnv *env, jobject obj) {

    List  * lista = (List *) malloc(sizeof(List));
    lista->total = 0;
    lista->start= NULL;
    lista->top = NULL;
    return (long)lista;

}

JNIEXPORT void JNICALL Java_com_avant_joao_avant_MainActivity_addItem(JNIEnv *env, jobject obj,jlong listReference, jdouble time,
                                               jdouble lenght, jchar foot) {

    ListItem *generatedList = (ListItem *) malloc(sizeof(ListItem));
    generatedList->time = time;
    generatedList->distance = lenght;
    generatedList->foot = foot;
    generatedList->next = NULL;

    appendList(   (List*) listReference , generatedList);

}

JNIEXPORT jdoubleArray JNICALL Java_com_avant_joao_avant_MainActivity_getList(JNIEnv *env, jobject obj, jlong listReference){

    List * lista = (List*)listReference;
    int size = lista->total;

    jdoubleArray tempos = (*env).NewDoubleArray(lista->total);

    jdouble fill[size];

    ListItem * current= lista->start;
    for (int i = 0; i < size; i++) {

        fill[i] = current->time;

        if(current->next != NULL){
            current= current->next;
        }
    }
    env->SetDoubleArrayRegion( tempos, 0, size, fill);

    return tempos;

}


}
