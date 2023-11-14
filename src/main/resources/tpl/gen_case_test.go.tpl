//go:build !live
// +build !live

package auto_test

import (
	"fmt"
	"os"
	"testing"

	${{import}}

	config_ext "git.garena.com/shopee/insurance/insurance-backend/insurance-framework/credit-framework-config-ext"
	"git.garena.com/shopee/insurance/insurance-backend/insurance-framework/gunit-trace-replay/feature"
	"git.garena.com/shopee/insurance/insurance-backend/insurance-framework/gunit-trace-replay/feature/cmd_server"
)

func TestGen(t *testing.T) {
	// eg, case name: ID_uat_InsuranceClaimManager.SubmitClaim_auto_idempotent
	// feature.GenCaseFile("ID", "uat", "InsuranceClaimManager.SubmitClaim", "idempotent", reqStr, getPathWithBase("InsuranceClaimManager/SubmitClaim"))

	reqStr := ``      //请求参数json字符串
	serviceName := "" //grpc服务名,与pb文件内一致，如: InsuranceClaimManager
	methodName := ""  //grpc方法名,与pb文件内一致，如: SubmitClaim
	region := ""      //地区编码，如：ID、MY
	caseSuffix := ""  //用例后缀名，用于区分不同用例，如：idempotent

	env := config_ext.GetRouterEnv()                                          //环境编码，如：test、uat，默认读取配置
	caseDir := getPathWithBase(fmt.Sprintf("%s/%s", serviceName, methodName)) //用例存储路径, 建议不改动
	if reqStr == "" {
		return
	}
	feature.GenCaseFile(region, env, fmt.Sprintf("%s.%s", serviceName, methodName), caseSuffix, reqStr, caseDir)
}

func TestRun(t *testing.T) {
	tcs := make([]*feature.TestCase, 0)
	feature.LoadAllReplayCase(getPathWithBase(""), &tcs)
	for _, tc := range tcs {
		feature.RunServiceTest(t, tc)
	}
}

func TestBoot(t *testing.T) {
	cmd_server.StartupServer(t)
}

func TestCmd(t *testing.T) {
	cmd_server.Cmd(t)
}

func getPathWithBase(path string) string {
	currentPath, _ := os.Getwd()
	return fmt.Sprintf("%s/cases/%s", currentPath, path)
}
