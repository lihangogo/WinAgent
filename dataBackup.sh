#!/bin/bash
# 数据库认证
user="root"
password="Lh991978"
host="10.108.101.237"
db_name="accelerator"
#保存历史数据的文件路径
backup_path="/usr/local/db_backup"
#日志文件的名称
log_name="backup.log"
#日志文件的路径
log=$backup_path/$log_name
#保存当前数据的文件路径
backup_path_now="/usr/local/$db_name.sql"
#要删除的备份文件名
backup_path_delete="$backup_path/$db_name-$(date -d "$days days ago" +"%Y-%m-%d").sql"

#文件名中时间格式
date=$(date +"%Y-%m-%d")
#设置导出文件的缺省权限
umask 177
#删除7天前的备份文件
days=7

#写入日志的函数
log()
{
   #当前时间
   time=$(date +"%Y-%m-%d %H:%M:%S")
   echo "$time -- $1" >> $log
}
#创建日志文件
if [ ! -f $log ]; then
   touch $log
   chmod 755 $log
fi

#导出远程数据库到本地SQL文件
mysqldump --user=$user --password=$password --host=$host $db_name > $backup_path_now
sleep 10
#拷贝当前SQL文件至历史数据文件夹中，用以备份
cp $backup_path_now $backup_path/$db_name-$date.sql
sleep 3
log "save backup file: $db_name-$date.sql successful"
#导入最新SQL文件到本地数据库
mysql -u $user -p$password -D $db_name < $backup_path_now
sleep 12
log "apply newest backup file: $db_name-$date.sql successful"

#删除过期备份文件
if [ -f $backup_path_delete ]; then
   rm $backup_path_delete
   log "delete backup file: $backup_path_delete ($days day(s) ago)"
fi



