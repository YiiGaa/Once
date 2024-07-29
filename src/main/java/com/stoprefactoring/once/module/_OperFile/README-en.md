# _OperFile-File operation

The main functions include:

- upload-upload file

- delete-delete file/directory

- move-move file/directory

Necessary modifications before the module is used:

- Set the parent directory of the file operation, `module._OperFile.rootPath` in file `_OperFile/Once.Config`, and ensure that the program has permission to operate this directory

Description of the value of module parameters:

- If the value you want to set is a fixed value, you can directly set the value of the corresponding type.

- If you want to dynamically obtain the value from `passParam` as the set value, please use `get##key` as the setting value of the module parameter.

- If the value you want to get is in the inner layer of passParam (nested json), please locate it with `>>`, such as `get##key_1>>key_2`

# ※ upload-upload file

- Add version: 1.0=2024.07.02
- It is only suitable for uploading small files. If it is a large file, please consider other solutions such as fragment uploading
- When requesting the API, please use the type `multipart/form-data` and record the file in the form
- Please consider these settings carefully: `_isExistCover`, `_isErrorRollBack`, `_isAutoRollBack`

```json
{
	"name":"_OperFile",
	"param":{
		"_action":"upload",
    "_targetKey":"file",
    "_resultPathKey":"filePath",
    "_isPutReturn":true
	}
}
```

| Key                    | Description                                                  | Necessary | Default                        | Type   | Add/Update     |
| ---------------------- | ------------------------------------------------------------ | --------- | ------------------------------ | ------ | -------------- |
| _action                | Specify the module action and fix it as "upload"             | Yes       |                                | String | 1.0=2024.07.02 |
| _targetKey             | The key corresponding to the upload file in the request form | No        | file                           | String | 1.0=2024.07.02 |
| _isNullError           | When the file cannot be obtained in the request form, whether to report an error. true: error, false: no error | No        | true                           | Bool   | 1.0=2024.07.02 |
| _maxCount              | Maximum number of files uploaded                             | No        | not limited                    | Int    | 1.0=2024.07.02 |
| _maxFileSize           | Maximum storage space limit for a single file (unit: Bytes)  | No        | not limited                    | Long   | 1.0=2024.07.02 |
| _AllowSuffix           | Allowed file suffixes, such as `".jpg|.png|.gif"`            | No        | not limited                    |        | 1.0=2024.07.02 |
| _targetFileName        | The saved file name, and the file suffix will be automatically spliced together, such as "/dirs/filename" | No        | uuid                           | String | 1.0=2024.07.02 |
| _isPutReturn           | Whether to put the result in `returnParam`, false: store the results in `passParam` | No        | true                           | Bool   | 1.0=2024.07.02 |
| _resultPathKey         | The key to put the result (uploaded file path)               | No        | Use the setting of `targetKey` | String | 1.0=2024.07.02 |
| _resultOriginalNameKey | The key to put the result (original file name)               | No        | Not recorded                   | String | 1.0=2024.07.02 |
| _resultFileSizeKey     | The key to put the result (file size),  unit: Bytes          | No        | Not recorded                   | String | 1.0=2024.07.02 |
| _isExistCover          | When the target path already exists, whether to overwrite, true: overwrite, false: error exit | No        | false                          | Bool   | 1.0=2024.07.02 |
| _isErrorRollBack       | When the module reports an error, whether to delete the uploaded file, true: automatic deletion, false: do not delete | No        | true                           | Bool   | 1.0=2024.07.02 |
| _isAutoRollBack        | When the business logic reports an error (Controller, Service calls other modules to report an error), whether to delete the uploaded files, true: automatic deletion, false: do not delete | No        | true                           | Bool   | 1.0=2024.07.02 |

Supplementary description of module parameters:

### > _targetKey

- The key corresponding to the upload file in the request form
- If multiple files are recorded with same key in the form, the module will automatically upload multiple files together
- If you upload multiple files, the module will automatically add the serial number identification before the file name suffix, such as `dirs/name-0.png`, `dirs/name-1.png`

### > _maxCount

- Maximum number of files uploaded
- This setting will only take effect when multiple files are recorded with same key in the request form, and the value is `> 0`

- If the value is `<1`, it is regarded as unlimited.

### > _maxFileSize

- Maximum storage space limit for a single file (unit: Bytes)
- This setting will only take effect when multiple files are recorded with multiple identical keys in the request form, and the value is > 0

- If the value is <1, it is regarded as unlimited

- Restrictions have priority, nginx/tomcat request data restrictions > spring.servlet.multipart.max-file-size > spring.servlet.multipart.max- File-size > module restrictions on upload file size

### > _AllowSuffix

- Allowed file suffixes, such as ".jpg"
- If there is no suffix, set it to `""`

- Set multiple file suffixes, separated by `|`, such as `".jpg|.png"`

- If you want to set it to the exclusion range, you can add the `not##` prefix, such as `"not##.jpg|.png"`

### > _targetFileName

- The saved file name, and the file suffix will be automatically spliced together, such as "/dirs/filename"
- The final file path is: `rootPath (configuration file) + _targetFileName + source file suffix (automatic acquisition)`

- Please use English, numbers and underline in the file name

- It is suggested that ``targetFileName` contains `uuid`, and update `uuid` every time the call module receives the uploaded file

- The file name can be set to a multi-layer directory, such as `/aa/bbb/cc`

- If multiple files are uploaded, the module will automatically add a serial number identifier before the file name suffix, such as `dirs/name_0.png`, `dirs/name_1.png`

### > _resultPathKey

- The key to put the result (uploaded file path)

- If the uploaded file is a single file, the result is:

    ```
    {
    	"result path key":"/rootPath/aa/bb.jpg"
    }
    ```

- If the uploaded file is multiple files, the serial number will be automatically added after the key, and the result is:

    ```
    {
    	"result path key0":"/rootPath/aa/bb_0.jpg",
    	"result path key1":"/rootPath/aa/bb_1.jpg",
    	"result path key2":"/rootPath/aa/bb_2.jpg"
    }
    ```

### > _resultOriginalNameKey

- The key to put the result (uploaded file path)

- Set to `""`, as unrecorded

- If the uploaded file is a single file, the result is:

    ```
    {
    	"result original name key":"originalName.jpg"
    }
    ```

- If the uploaded file is multiple files, the serial number will be automatically added as the logo after the key. The order is the same as the record of `_resultPathKey`

    ```
    {
    	"result original name key0":"aa.jpg",
    	"result original name key1":"bb.jpg",
    	"result original name key2":"cc.jpg"
    }
    ```

### > _resultFileSizeKey

- The key to put the result (file size)

- The result is Long type data (unit: Bytes)

- Set to `""`, as unrecorded

- If the uploaded file is a single file, the result is:

    ```
    {
    	"result file size key": 760462
    }
    ```

- If the uploaded file is multiple files, the serial number will be automatically added as the logo after the key. The order is the same as the record of `_resultPathKey`

    ```
    {
    	"result path key0":760462,
    	"result path key1":790960462,
    	"result path key2":790960462879
    }
    ```

### > _isExistCover

- When the target path already exists, whether to overwrite, true: overwrite, false: error exit
- When `_isErrorRollBack` or `_isAutoRollBack` is true, `_isExistCover` is not suggested to set to true, because it may occur: After the old file is overwritten, the file is automatically deleted due to an error

### > _isErrorRollBack

- When the module reports an error, whether to delete the uploaded file, true: automatic deletion, false: do not delete
- Automatic deletion of files is executed by asynchronous threads, so the file may not have been deleted when the module returns

- When `_isErrorRollBack` is set to true, it is not suggested to set `_isExistCover` to true. If both are true, there may be cases of uploading and overwriting the old file, and the automatically deleted asynchronous thread deleting the new file

### > _isAutoRollBack

- When the business logic reports an error (Controller, Service calls other modules to report an error), whether to delete the uploaded files, true: automatic deletion, false: do not delete
- When an interface uses multiple Service logic, and Controller and multiple Services call the module to upload the file, when a Service business logic reports an error, it will only delete the file uploaded by this Service, the other  file uploaded by other Service or Controller will not be deleted. When the Controller business logic ends the error report, all files uploaded by Controller and Service will be deleted
- Automatic deletion of files is executed by asynchronous threads, and the file may not have been deleted when Controller or Service return
- When `_isAutoRollBack` is set to true, it is not recommended to set `_isExistCover` to true. If both are true, there may be cases where the automatically deleted asynchronous thread deletes the new file after uploading and overwriting the old file

# ※ delete-delete file/directory

- Add version: 1.0=2024.07.02
- Please consider carefully. After deleting the file, it is generally not possible to roll back/recome.

```json
{
	"name":"_OperFile",
	"param":{
		"_action":"delete",
    "_path":""
	}
}
```

| key                     | Description                                                  | Necessary | Default | Type   | Add/Update     |
| ----------------------- | ------------------------------------------------------------ | --------- | ------- | ------ | -------------- |
| _action                 | Specify the module action and fix it as "delete"             | Yes       |         | String | 1.0=2024.07.02 |
| _path                   | File/directory path to be deleted                            | Yes       |         | String | 1.0=2024.07.02 |
| _isNullError            | When the file/directory does not exist, whether an error is reported, true: error, false: no error | No        | true    | Bool   | 1.0=2024.07.02 |
| _isSync                 | Whether to delete in a synchronous way, true: delete synchronously, false: asynchronous thread deletion | No        | false   | Bool   | 1.0=2024.07.02 |
| _isDeleteErrorInterrupt | When deleting the directory, if there is an error in the middle, whether it will be interrupted. True: interrupt, false: continue | No        | false   | Bool   | 1.0=2024.07.02 |

Supplementary description of module parameters:

### > _path

- File/directory path to be deleted
- For file security, the beginning of the path is required to match the configuration of `module. _OperFile.rootPath` or `module._OperFile.checkPath`, otherwise an error will be reported
- Please plan the directory structure used to receive uploaded files

### > _isSync

- Whether to delete in a synchronous way, true: delete synchronously, false: asynchronous thread deletion
- When the file is small, it can be deleted synchronously
- When the file is large or may contain large files, it is recommended to use asynchronous thread deletion. Because it takes a long time to delete large files synchronously, which may cause problems such as api timeout
- When deleting a directory, it is recommended to use asynchronous thread deletion, because for deleting the directory, it is actually a recursive deletion of each file in the directory
- Asynchronous deletion is completed by a thread pool. In the scenario of high concurrency, a lot of deletion tasks to be processed may be piled up. Before closing the back-end application, you should stop receiving the request of the back-end application (gateway interception, etc.) and wait for 1-2 hours before closing the back-end application. Otherwise, there is a high probability that the deleted task will be lost. Of course, waiting for 1-2 hours does not guarantee that all tasks will be completed
- If you want to ensure that you don't lose any delete tasks, you need to adopt other solutions, such as executing cloud computing tasks by Hive (stoprefactoring.com)
- If it is deleted synchronously, the module will return the deletion result. If it is deleted asynchronously, it can only confirm whether the deletion is successful through the log

### > _isDeleteErrorInterrupt

- When deleting the directory, if there is an error in the middle, whether it will be interrupted. True: interrupt, false: continue
- When deleting a directory, it is actually a recursive deletion of each file in the directory. Some files may not be deleted due to permissions and other problems. if `_isDeleteErrorInterrupt` is set to true, and it will interrupt and exit. If it is set to false, then continue to try to delete other files
- When deleted in a synchronous way, no matter how `_isDeleteErrorInterrupt` is set, an error will be returned

# ※ move-move file/directory

- Add version: 1.0=2024.07.02
- For files that need to be deleted, it is more recommended to move the files to a specific directory (equivalent to a recycle bin) first, and then clean up the files through timed tasks and other means after a certain period of time
- If it is under the same file system, the migration file/directory can be simply understood as renaming

```json
{
	"name":"_OperFile",
	"param":{
		"_action":"move",
    "_sourcePath":"",
    "_targetPath":""
	}
}
```

| key                | Description                                                  | Necessary | Default | Type   | Add/Update     |
| ------------------ | ------------------------------------------------------------ | --------- | ------- | ------ | -------------- |
| _action            | Specify themodule action and fix it as "action"              | Yes       |         | String | 1.0=2024.07.02 |
| _sourcePath        | Source file/directory path                                   | Yes       |         | String | 1.0=2024.07.02 |
| _targetPath        | Target file/directory path                                   | Yes       |         | String | 1.0=2024.07.02 |
| _isSourceNullError | When the source file/directory does not exist, whether the error is reported, true: error, false: no error | No        | true    | Bool   | 1.0=2024.07.02 |
| _isTargetCover     | When the target file/directory path exists, whether to cover, true: cover, false: do not cover and report an error | No        | false   | Bool   | 1.0=2024.07.02 |
| _isSync            | Whether to move file/directory in a synchronous way, true: synchronous move, false: asynchronous thread move | No        | false   | Bool   | 1.0=2024.07.02 |

Supplementary description of module parameters:

### > _sourcePath

- Source file/directory path
- For file security, the beginning of the path is required to match the configuration of `module. _OperFile.rootPath` or `module._OperFile.checkPath`, otherwise an error will be reported

### > _sourcePath

- Target file/directory path
- For file security, the beginning of the path is required to match the configuration of `module. _OperFile.rootPath` or `module._OperFile.checkPath`, otherwise an error will be reported

### > _isTargetCover

- When the target file/directory path exists, whether to cover, true: cover, false: do not cover and report an error
- When the target path already exists and is a directory, it cannot be covered, and an error will still be reported to exit

### > _isSync

- Whether to move file/directory in a synchronous way, true: synchronous move, false: asynchronous thread move
- If files/folders are moved under the same file system, it can generally be synchronized
- If you move files/folders under a non-same file system, it may take some time, and it is more recommended to use the asynchronous thread
- Asynchronous move is completed by a thread pool. In the scenario of high concurrency, there may be a lot of move tasks to be processed. Before closing the back-end application, you should stop receiving requests from the back-end application (gateway interception, etc.) and wait for 1-2 hours before closing the back-end application. Otherwise, it is likely to lead to the loss of migration tasks. Of course, waiting for 1-2 hours does not guarantee that all tasks will be completed
- If you want to ensure that all move tasks are not lost, you need to adopt other solutions, such as cloud computing tasks under the Hive (stoprefactoring.com) architecture
- 同步迁移，模块会返回迁移结果，而异步迁移，则只能通过日志确认是否迁移成功
- If you want to ensure that you don't lose any move tasks, you need to adopt other solutions, such as executing cloud computing tasks by Hive (stoprefactoring.com)
- If it is move synchronously, the module will return the deletion result. If it is move asynchronously, it can only confirm whether the deletion is successful through the log

# ◎ Configuration Notes

The module configuration is set in `_OperFile/Once.Config`. When the application is run through Christmas, it will be automatically spliced to `config/application.properties`

| key                                       | Description                                                  | Necessary | Default     | Type   | Add/Update     |
| ----------------------------------------- | ------------------------------------------------------------ | --------- | ----------- | ------ | -------------- |
| spring.servlet.multipart.max-file-size    | Limit single file size in advance by servlet，如1MB          | No        | 1MB         | String | SpringBoot |
| spring.servlet.multipart.max-request-size | Limit the size of the entire requested data through servlet  | No        | 10MB        | String | SpringBoot |
| module._OperFile.rootPath                 | The parent directory of the upload file, and this setting is also used as the parent directory that allows movement and deletion | Yes       |             | String | 1.0=2024.07.02 |
| module._OperFile.checkPath                | Parent directories that are allowed to move and delete       | No        |             | String | 1.0=2024.07.02 |
| module._OperFile.poolSize                 | Set the maximum number of threads in the thread pool for asynchronous deletion and moving files | No        | 20          | Int    | 1.0=2024.07.02 |
| module._OperFile.poolQueue                | Set the number of queue of thread pools for asynchronous deletion and moving files | No        | not limited | Int    | 1.0=2024.07.02 |

Supplementary description of configuration:

### > spring.servlet.multipart.max-file-size

- Limit single file size in advance by servlet，如1MB
- This setting is the setting of `spring.servlet`, and the default is 1MB (single file)
- If the file is larger than this setting, a 413 error will be returned, and the Controller will not receive the request

- Restrictions have priority, nginx/tomcat request data restrictions > spring.servlet.multipart.max-file-size > spring.servlet.multipart.max- File-size > module restrictions on upload file size

### > spring.servlet.multipart.max-request-size

- Limit the size of the entire requested data through servlet
- This setting is the setting of spring.servlet, and the default is 10MB.

- If the form contains multiple files, please modify this value as needed
- Restrictions have priority, nginx/tomcat request data restrictions > spring.servlet.multipart.max-file-size > spring.servlet.multipart.max- File-size > module restrictions on upload file size

### > module._OperFile.rootPath

- The parent directory of the upload file, and this setting is also used as the parent directory that allows movement and deletion
- When uploading a file, this parent directory is mandatory to be added
- Please set the file operation permissions

### > module._OperFile.checkPath

- Parent directories that are allowed to move and delete
- If it is not set, then only allowed parent directory is `module._OperFile.rootPath`

- Allow multiple values to be set, separated by `,` , such as `/data,/backup`

- Please set the file operation permissions

### > module._OperFile.poolQueue

- Set the number of queue of thread pools for asynchronous deletion and moving files
- If the queue is full, the module will report an error

# ◎ Module test separately

Modify the `module. _OperFile.rootPath` in the `_OperFile/Once.Config` file before testing (for the parent directory of the upload file)

Then, you need to run the module separately. In the project root directory, run the shell instruction

```
python3 Christmas/Christmas.py ShellExcute/Compile#TestModule _OperFile
```

You can also run ShellExcute>>Compile#TestModule through the Christmas plug-in, and enter `_OperFile` in the terminal opened by the plug-in.

### > Sample-upload

- URL: http://127.0.0.1:8080/once/module/sample/upload

- Request method: POST

- Content type: multipart/form-data

    ```
    The key to record the file in the form:file
    File less than 2MB
    ```

- Expected results: There is a file in the parent directory ( `module._OperFile.rootPath`), Request return:

    ```
    {
        "result": "/data/11db62b64c4c4f36b4d0c52a5fa33d5b.png",
        "name": "orginalname.png",
        "size": 1193615,
        "code": "200",
        "message": "OK"
    }
    ```

### > Sample-delete

- URL: http://127.0.0.1:8080/once/module/sample/delete

- Request method: POST

- Content type: application/json

    ```
    {
    	"file":"/data/11db62b64c4c4f36b4d0c52a5fa33d5b.png(The path returned when uploading)"
    }
    ```

- Expected results: The file was deleted, Request return:

    ```
    {
        "code": "200",
        "message": "OK"
    }
    ```

### > Sample-move

- URL: http://127.0.0.1:8080/once/module/sample/move

- Request method: POST

- Content typ: application/json

    ```
    {
    	"source":"/data/11db62b64c4c4f36b4d0c52a5fa33d5b.png(The path returned when uploading)",
    	"target":"/data/11db62b64c4c4f36b4d0c52a5fa33d5b.png(target path)"
    }
    ```

- Expected results: The file has been moved, Request return:

    ```
    {
        "code": "200",
        "message": "OK"
    }
    ```

# ◎ Update list

**1.0=2024.07.02**

- Module create