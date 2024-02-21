#include <linux/module.h>
#include <linux/version.h>
#include <linux/kernel.h>
#include <linux/types.h>
#include <linux/kdev_t.h>
#include <linux/fs.h>
#include <linux/device.h>
#include <linux/cdev.h>

#define BUF_SIZE 256

static dev_t first;
static struct cdev c_dev; 
static struct class * cl;

char ibuf[BUF_SIZE];
char res_buf[BUF_SIZE];
int cnt = 0;


static int my_open(struct inode *i, struct file *f)
{
  printk(KERN_INFO "Driver: open()\n");
  return 0;
}

static int my_close(struct inode *i, struct file *f)
{
  printk(KERN_INFO "Driver: close()\n");
  return 0;
}

void convert_int(int cnt, char res_buf[BUF_SIZE]) {
    int i = 0; 
    while (cnt > 0) {
        res_buf[i++] = (cnt % 10) + '0';
        cnt = cnt / 10;
    }
    int last = i - 1;
    while (last >= i/2) {
        char tmp = res_buf[last];
        res_buf[last] = res_buf[i - 1 - last];
        res_buf[i - 1 - last] = tmp;
        last--;
    }
    res_buf[i++] = '\n';
    res_buf[i] = '\0';
}

static ssize_t my_read(struct file *f, char __user *buf, size_t len, loff_t *off)
{
  
  printk(KERN_INFO "Driver: read()\n");
  
  convert_int(cnt, res_buf);
  int count = strlen(res_buf);

    if (*off > 0 || len < count) {
        return 0;
    }

  if (copy_to_user(buf, res_buf, count) != 0) {
      return -EFAULT;
  }

  *off = count;
  
  return count;
}

static ssize_t my_write(struct file *f, const char __user *buf,  size_t len, loff_t *off)
{
  printk(KERN_INFO "Driver: write()\n");
  
  if(len > BUF_SIZE)
      return 0;
  
  if (copy_from_user(ibuf, buf, len) != 0) {
      return -EFAULT;
  }
    int i = 0;
    while (i < len) {
        if (ibuf[i] >= 33 && ibuf[i] <= 126) {
            cnt += 1;
        }
        i++;
    }

  return len;
}

static struct file_operations mychdev_fops =
{
  .owner = THIS_MODULE,
  .open = my_open,
  .release = my_close,
  .read = my_read,
  .write = my_write
};
 
static int __init ch_drv_init(void)
{
    if (alloc_chrdev_region(&first, 0, 1, "ch_dev") < 0)
	  {
		return -1;
	  }
    if ((cl = class_create(THIS_MODULE, "chardrv")) == NULL)
	  {
		unregister_chrdev_region(first, 1);
		return -1;
	  }
      
      if (device_create(cl, NULL, first, NULL, "var1") == NULL)
      {
          class_destroy(cl);
          unregister_chrdev_region(first, 1);
          return -1;
      }
    
      
    cdev_init(&c_dev, &mychdev_fops);
    if (cdev_add(&c_dev, first, 1) == -1)
	  {
		device_destroy(cl, first);
		class_destroy(cl);
		unregister_chrdev_region(first, 1);
		return -1;
	  }
    return 0;
}
 
static void __exit ch_drv_exit(void)
{
    cdev_del(&c_dev);
    device_destroy(cl, first);
    class_destroy(cl);
    unregister_chrdev_region(first, 1);
    printk(KERN_INFO "Bye!!!\n");
}
 
module_init(ch_drv_init);
module_exit(ch_drv_exit);
 
MODULE_LICENSE("GPL");
MODULE_AUTHOR("Author");
MODULE_DESCRIPTION("The first kernel module");
