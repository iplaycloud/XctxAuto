//
// Created by Administrator on 2016/6/8.
//
#include <jni.h>
#include <android/log.h>

#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/ioctl.h>
#include <fcntl.h>
#include <math.h>

#include <termios.h>
#include <linux/input.h>

#define UART_DEVICE     "/dev/ttyMT1" //uart设备文件名

#define  LOG_TAG    "NativeBtLib"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)


#include <jni.h>
#include <string>

int set_serial_port_baudrate(int fd) {
    struct termios opt;
    int ifInitFail = 0;

    if (fd < 0) {
        LOGI("set serial port failed because serial fd not exist");
        return -1;
    }
    tcgetattr(fd, &opt);
    cfsetispeed(&opt, B115200);
    cfsetospeed(&opt, B115200);
    if (tcsetattr(fd, TCSANOW, &opt) != 0) {
        ifInitFail = 0x1;
        LOGI("tcsetattr error");
        return -1;
    }

    tcflush(fd, TCIOFLUSH);
    tcgetattr(fd, &opt);

    opt.c_iflag &= ~(IGNBRK | BRKINT | PARMRK | ISTRIP
                     | INLCR | IGNCR | ICRNL | IXON);
    opt.c_oflag &= ~OPOST;
    opt.c_lflag &= ~(ECHO | ECHONL | ICANON | ISIG | IEXTEN);
    opt.c_cflag &= ~(CSIZE | PARENB);
    opt.c_cflag |= CS8;

    //#if GPS_UART_RTS_CTS_FLOW_CONTROL_ENABLE
    //opt.c_cflag |= CRTSCTS;
    //#else
    opt.c_cflag &= ~CRTSCTS;
    //#endif

    tcsetattr(fd, TCSANOW, &opt);
    tcflush(fd, TCIOFLUSH);
    tcsetattr(fd, TCSANOW, &opt);
    tcflush(fd, TCIOFLUSH);
    tcflush(fd, TCIOFLUSH);

    cfsetospeed(&opt, B115200);
    cfsetispeed(&opt, B115200);
    tcsetattr(fd, TCSANOW, &opt);

    opt.c_cc[VTIME] = 0;
    opt.c_cc[VMIN] = 0;
    tcflush(fd, TCIOFLUSH);
    LOGI("configure complete\n");
    if (tcsetattr(fd, TCSANOW, &opt) != 0) {
        ifInitFail = 0x1;
        LOGI("serial error");
        return -1;
    }
    return 0;

}

extern "C"
int uart_init(const char *path) {
    int fd;

    static struct termios newtio;

    /*--打开uart设备文件--*/
    /*没有设置O_NONBLOCK，所以这里read和write是阻塞操作*/
    //fd = open(UART_DEVICE, O_RDWR|O_NOCTTY);
    fd = open(path, O_RDWR | O_NOCTTY);
    if (fd < 0) {
        perror(UART_DEVICE);
        return fd;
    }
    else {
        printf("open %s successfully\n", UART_DEVICE);
    }

    set_serial_port_baudrate(fd);

    return fd;
}

extern "C"
jint
Java_com_xctx_rearviewMirror_jni_btmodule_NativeBtLib_openUart(JNIEnv *env, jobject /* this */) {

    jint fd = 0;

    fd = uart_init(UART_DEVICE);

    return fd;
}

extern "C"
jbyteArray Java_com_xctx_rearviewMirror_jni_btmodule_NativeBtLib_readUart(JNIEnv *env,
                                                                          jobject thiz,
                                                                          jint fd,
                                                                          jint count) {
    int ret = 0;

    char buf[128];

    if (count == 0)
        count = 128;

    if (count > 128)
        count = 128;

    ret = read(fd, buf, count);
    if (ret == 0)
        return NULL;

    buf[ret] = '\0';
    LOGI("read : fd = %d, ret = %d, buf = %s", fd, ret, buf);

    /*分配Java层的byte数组*/
    jbyteArray array = env->NewByteArray(ret);

    /*把buf数据拷贝到array中*/
    env->SetByteArrayRegion(array, 0, ret, (const jbyte *) buf);

    return array;
}


extern "C"
jint Java_com_xctx_rearviewMirror_jni_btmodule_NativeBtLib_writeUart(JNIEnv *env,
                                                                     jobject thiz, jint fd,
                                                                     jbyteArray byarray,
                                                                     jint count) {
    int ret = 0;

    jsize len = env->GetArrayLength(byarray);

    jbyte *jbarray = (jbyte *) malloc(len * sizeof(jbyte));

    env->GetByteArrayRegion(byarray, 0, len, jbarray);

    char *dDate = (char *) jbarray;

    LOGI("write : %s\n", dDate);

    ret = write(fd, dDate, len);

    return ret;
}

extern "C"
jint Java_com_xctx_rearviewMirror_jni_btmodule_NativeBtLib_closeUart(JNIEnv *env,
                                                                     jobject thiz, jint fd) {

    close(fd);

}



