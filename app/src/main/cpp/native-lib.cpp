#include <jni.h>
#include <string>
#include <list>
#include <android/log.h>
#include <vector>

int index = 0; // To keep the current ID

class Node { // The linked list class
public:
    int id; // The data
    Node* next; // The pointer to the next node
};

Node *head= new Node(); // Create the head node
Node *current= head; // Pointer to the current node
std::vector<std::vector<int>> rgbList; // A list to keep the RGB values

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_randomcolourplayer_MainActivity_stringFromJNI( // The native method to return the current ID to the Java side
        JNIEnv* env,
        jobject /* this*/ ) {
    std::string h = std::to_string(index);
    return env->NewStringUTF(h.c_str());
}

int getRandomRGBValue() { // Every R/G/B value is generated by rand() function (Excluding black)
    std::uint8_t val = std::rand()%254+1;
    return val;
}

extern "C" JNIEXPORT jintArray JNICALL
Java_com_example_randomcolourplayer_MainActivity_insertNode( // Insert a new node next to the current node in the linked list
        JNIEnv* env,
        jobject /* this */) {
    int length = 3;
    Node* newNode = new Node(); // Create a new node
    index++; // Increase the ID
    newNode->id = index; // Set the ID
    current->next = newNode; // Point to the new node
    current = current->next; // Now the new node will be the current node
    jintArray intJavaArray = env->NewIntArray(length); // The array for returning data to java side
    jint *intCArray = env->GetIntArrayElements(intJavaArray, NULL);
    std::vector<int> rgb;
    for(int j = 0; j<length ; j++){
        intCArray[j] = getRandomRGBValue();
        rgb.push_back(intCArray[j]);
    }
    env->SetIntArrayRegion(intJavaArray, 0, length, intCArray);
    for (auto v : rgb)
        __android_log_print(ANDROID_LOG_DEBUG, "HELLO", "%d", v);
    rgbList.push_back(rgb);
    return intJavaArray;
}
extern "C" JNIEXPORT void JNICALL
Java_com_example_randomcolourplayer_MainActivity_insertNodeAtTheEnd( // Insert a new node to the end of the linked list
        JNIEnv* env,
        jobject /* this */) {
    int length = 3;
    Node *tail= head;
    Node* newNode = new Node(); // Create a new node
    index++; // Increase the ID
    newNode->id = index; // Set the ID
    while(tail->next!=NULL)
        tail = tail->next; // Go to the last node from the head
    tail->next = newNode;
    current = tail->next;
    std::vector<int> rgb;
    for(int j = 0; j<length; j++){
        rgb.push_back(0);
    }
    rgbList.push_back(rgb);
}

extern "C" JNIEXPORT jintArray JNICALL
Java_com_example_randomcolourplayer_MainActivity_replay(
        JNIEnv* env,
        jobject /* this */) {
    int length = rgbList.size() * 3;
    // Concatenate all elements in the vector in an array
    // Each element contains 3 values (RGB)
    jintArray intJavaArray = env->NewIntArray(length); // The array for returning data to java side
    jint *intCArray = env->GetIntArrayElements(intJavaArray, NULL);
    int i = 0;
    Node *newHead = head;
    while(newHead!=NULL && newHead->next!=NULL){
        newHead = newHead->next;
        __android_log_print(ANDROID_LOG_DEBUG, "HELLO", "%d", newHead->id);
        __android_log_print(ANDROID_LOG_DEBUG, "HELLO", "%d %d %d",
                            rgbList[newHead->id-1][0],
                            rgbList[newHead->id-1][1],
                            rgbList[newHead->id-1][2]);
        intCArray[i++] = rgbList[newHead->id-1][0];
        intCArray[i++] = rgbList[newHead->id-1][1];
        intCArray[i++] = rgbList[newHead->id-1][2];
    }
    env->SetIntArrayRegion(intJavaArray, 0, length, intCArray);
    return intJavaArray;
}