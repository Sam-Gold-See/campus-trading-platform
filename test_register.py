import requests
import time
import json

BASE_URL = "http://localhost:8080/api"

def test_send_verification_code(email):
    """测试发送注册验证码"""
    url = f"{BASE_URL}/user/sendRegisterCode"
    payload = {"eduEmail": email}

    print(f"正在发送验证码到邮箱: {email}")
    response = requests.post(url, json=payload)
    print(f"发送验证码响应: {response.status_code}")
    print(f"响应内容: {response.text}")
    return response

def test_register_user(email, verification_code, nickname, password):
    """测试用户注册"""
    url = f"{BASE_URL}/user/register"
    payload = {
        "eduEmail": email,
        "verificationCode": verification_code,
        "nickname": nickname,
        "password": password,
        "confirmPassword": password
    }

    print(f"正在注册用户: {email}")
    response = requests.post(url, json=payload)
    print(f"注册响应: {response.status_code}")
    print(f"响应内容: {response.text}")
    return response

def manual_test():
    """手动测试流程 - 需要预先在Redis中设置验证码"""
    print("=== 手动注册功能测试 ===")

    # 使用测试邮箱
    test_email = "test@register.edu.cn"
    test_nickname = "测试用户2026"
    test_password = "Test1234"

    # 先尝试发送验证码
    send_response = test_send_verification_code(test_email)

    if send_response.status_code == 200:
        print("\n验证码发送成功！")
        print("注意：由于这是一个真实的API调用，你需要等待验证码发送")
        print("或者你可以在Redis中手动设置验证码来继续测试注册流程")
        print("Redis命令示例: SET register:test@register.edu.cn '123456' EX 300")

        # 如果是在真实环境中，我们会等待用户输入验证码
        # 但在这里，我们模拟一个场景
        print("\n为了完整测试，我们需要有效的验证码。")
        print("如果你无法接收邮件，可以使用Redis CLI手动设置验证码:")
        print("1. 连接到Redis: redis-cli")
        print("2. 设置验证码: SET register:email:test@register.edu.cn '123456' EX 300")
        print("3. 然后运行注册测试")
    else:
        print(f"\n验证码发送失败: {send_response.status_code}")

def simulate_register_flow():
    """模拟注册流程（使用预设验证码）"""
    print("\n=== 模拟注册流程测试 ===")

    # 假设我们已经在Redis中设置了验证码
    test_email = "test@register.edu.cn"
    test_verification_code = "123456"  # 模拟的验证码
    test_nickname = "测试用户2026"
    test_password = "Test1234"

    print(f"使用模拟验证码进行注册...")
    register_response = test_register_user(
        test_email,
        test_verification_code,
        test_nickname,
        test_password
    )

    if register_response.status_code == 200:
        print("模拟注册成功!")
        try:
            result = json.loads(register_response.text)
            print(f"用户ID: {result['data']['id']}")
            print(f"用户昵称: {result['data']['nickname']}")
            print(f"Token: {result['data']['token'][:20]}...")  # 只显示前20个字符
        except:
            print("无法解析响应数据")
    else:
        print(f"模拟注册失败: {register_response.status_code}")
        print(f"错误信息: {register_response.text}")

if __name__ == "__main__":
    print("开始测试注册功能...")

    # 执行手动测试
    manual_test()

    # 询问用户是否要执行模拟测试
    print("\n" + "="*50)
    response = input("是否要执行模拟注册流程测试？(y/n): ").lower().strip()
    if response in ['y', 'yes']:
        simulate_register_flow()