//go:build !live
// +build !live

package pre

import (
	_ "git.garena.com/shopee/insurance/insurance-backend/insurance-framework/credit-framework-config-ext"

	_ "git.garena.com/shopee/insurance/insurance-backend/insurance-framework/gunit"
	_ "git.garena.com/shopee/insurance/insurance-backend/insurance-framework/gunit-trace-replay"

	${{imports}}

	"git.garena.com/shopee/insurance/insurance-backend/insurance-framework/gunit-trace-replay/feature"
	"git.garena.com/shopee/insurance/insurance-backend/insurance-framework/gunit-trace-replay/replay_data"
)

func init() {
	replay_data.AddEquivalentWords([]string{
		"test.01", "uat.01", "live",
	})

	//业务服务启动时，预加载函数，如本地缓存加载、定时任务注册，与main.go一致
	${{preLoad}}

	//PreRunFunc 执行用例前执行（生成用例时不执行）时，用于服务自定义Mock函数等特殊场景
	feature.SetPreRunFunc(func() {
		/* eg:
		gunit.AddMethodPatches(func() interface{} {
			return &httpImpl.RemoteCilentImpl{}
		}, "DoHttp", func(self *httpImpl.RemoteCilentImpl, ctx context.Context, param interface{}) error {
			return nil
		})
		*/
	})
}
