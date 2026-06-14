import sys
import os

class Logger:
    @staticmethod
    def _CheckColorSupport():
        #STEP::Check environ
        if os.environ.get('NO_COLOR'):
            return False
        if os.environ.get('FORCE_COLOR'):
            return True
        
        #STEP::Check stdout support
        if not sys.stdout.isatty():
            return False
        
        #WHEN::<Windows 10
        if sys.platform == 'win32':
            try:
                import platform
                win_ver = platform.version()
                major = int(win_ver.split('.')[0]) if win_ver else 0
                return major >= 10
            except:
                return False
        return True
    
    #TIPS::Normal print
    @staticmethod
    def Info(text):
        print(text)
    
    #TIPS::Yellow text
    @staticmethod
    def Tips(text):
        if not Logger._CheckColorSupport():
            print(text)
            return
        print(f'\033[33m{text}\033[0m')

    #TIPS::Red text and exit
    @staticmethod
    def Error(text):
        if not Logger._CheckColorSupport():
            print(text)
        else:
            print(f'\033[31m{text}\033[0m')
        exit(-1)