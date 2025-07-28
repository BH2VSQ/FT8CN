package com.bg7yoz.ft8cn.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bg7yoz.ft8cn.GeneralVariables;
import com.bg7yoz.ft8cn.MainViewModel;
import com.bg7yoz.ft8cn.R;
import com.bg7yoz.ft8cn.databinding.FragmentAdvancedConfigBinding;
import com.bg7yoz.ft8cn.database.DatabaseOpr;

/**
 * 高级配置界面
 * 支持多槽信息、远征模式、比赛模式、队列模式等高级功能
 */
public class AdvancedConfigFragment extends Fragment {
    private FragmentAdvancedConfigBinding binding;
    private MainViewModel mainViewModel;
    private DatabaseOpr databaseOpr;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdvancedConfigBinding.inflate(inflater, container, false);
        mainViewModel = MainViewModel.getInstance(this);
        databaseOpr = mainViewModel.databaseOpr;

        initUI();
        setupListeners();
        
        return binding.getRoot();
    }

    private void initUI() {
        // 初始化UI控件状态
        binding.multiSlotModeSwitch.setChecked(GeneralVariables.multiSlotMode);
        binding.foxModeSwitch.setChecked(GeneralVariables.foxMode);
        binding.houndModeSwitch.setChecked(GeneralVariables.houndMode);
        binding.contestModeSwitch.setChecked(GeneralVariables.contestMode);
        binding.queueModeSwitch.setChecked(GeneralVariables.queueMode);
        binding.dynamicMessageSwitchSwitch.setChecked(GeneralVariables.dynamicMessageSwitch);
        binding.noAutoSwitchToCallSwitch.setChecked(GeneralVariables.noAutoSwitchToCall);
        
        binding.contestModifierEdit.setText(GeneralVariables.contestModifier);
        binding.maxQueueSizeEdit.setText(String.valueOf(GeneralVariables.maxQueueSize));
        
        updateQueueInfo();
    }

    private void setupListeners() {
        // 多槽信息模式
        binding.multiSlotModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                GeneralVariables.multiSlotMode = isChecked;
                writeConfig("multiSlotMode", String.valueOf(isChecked));
                ToastMessage.show(isChecked ? "已开启多槽信息模式" : "已关闭多槽信息模式");
            }
        });

        // 狐狸模式
        binding.foxModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                GeneralVariables.foxMode = isChecked;
                writeConfig("foxMode", String.valueOf(isChecked));
                
                if (isChecked) {
                    GeneralVariables.houndMode = false;
                    binding.houndModeSwitch.setChecked(false);
                    writeConfig("houndMode", "false");
                }
                
                ToastMessage.show(isChecked ? "已开启狐狸模式" : "已关闭狐狸模式");
            }
        });

        // 猎犬模式
        binding.houndModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                GeneralVariables.houndMode = isChecked;
                writeConfig("houndMode", String.valueOf(isChecked));
                
                if (isChecked) {
                    GeneralVariables.foxMode = false;
                    binding.foxModeSwitch.setChecked(false);
                    writeConfig("foxMode", "false");
                }
                
                ToastMessage.show(isChecked ? "已开启猎犬模式" : "已关闭猎犬模式");
            }
        });

        // 比赛模式
        binding.contestModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                GeneralVariables.contestMode = isChecked;
                writeConfig("contestMode", String.valueOf(isChecked));
                ToastMessage.show(isChecked ? "已开启比赛模式" : "已关闭比赛模式");
            }
        });

        // 队列模式
        binding.queueModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                GeneralVariables.queueMode = isChecked;
                writeConfig("queueMode", String.valueOf(isChecked));
                ToastMessage.show(isChecked ? "已开启队列模式" : "已关闭队列模式");
            }
        });

        // 动态消息切换
        binding.dynamicMessageSwitchSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                GeneralVariables.dynamicMessageSwitch = isChecked;
                writeConfig("dynamicMessageSwitch", String.valueOf(isChecked));
                ToastMessage.show(isChecked ? "已开启动态消息切换" : "已关闭动态消息切换");
            }
        });

        // 不自动切换到呼叫界面
        binding.noAutoSwitchToCallSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                GeneralVariables.noAutoSwitchToCall = isChecked;
                writeConfig("noAutoSwitchToCall", String.valueOf(isChecked));
                ToastMessage.show(isChecked ? "已禁用自动切换到呼叫界面" : "已启用自动切换到呼叫界面");
            }
        });

        // 比赛修饰符
        binding.contestModifierEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String modifier = s.toString().toUpperCase().trim();
                if (modifier.matches("[0-9]{3}|[A-Z]{1,4}|")) {
                    binding.contestModifierEdit.setTextColor(getContext().getColor(R.color.text_view_color));
                    GeneralVariables.contestModifier = modifier;
                    writeConfig("contestModifier", modifier);
                } else {
                    binding.contestModifierEdit.setTextColor(getContext().getColor(R.color.text_view_error_color));
                }
            }
        });

        // 最大队列大小
        binding.maxQueueSizeEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    int size = Integer.parseInt(s.toString());
                    if (size > 0 && size <= 50) {
                        binding.maxQueueSizeEdit.setTextColor(getContext().getColor(R.color.text_view_color));
                        GeneralVariables.maxQueueSize = size;
                        writeConfig("maxQueueSize", String.valueOf(size));
                    } else {
                        binding.maxQueueSizeEdit.setTextColor(getContext().getColor(R.color.text_view_error_color));
                    }
                } catch (NumberFormatException e) {
                    binding.maxQueueSizeEdit.setTextColor(getContext().getColor(R.color.text_view_error_color));
                }
            }
        });

        // 清空队列按钮
        binding.clearQueueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeneralVariables.callsignQueue.clear();
                updateQueueInfo();
                ToastMessage.show("队列已清空");
            }
        });
    }

    private void updateQueueInfo() {
        String queueInfo = "当前队列: " + GeneralVariables.callsignQueue.size() + "/" + GeneralVariables.maxQueueSize;
        if (!GeneralVariables.callsignQueue.isEmpty()) {
            queueInfo += "\n" + String.join(", ", GeneralVariables.callsignQueue);
        }
        binding.queueInfoText.setText(queueInfo);
    }

    private void writeConfig(String key, String value) {
        if (databaseOpr != null) {
            databaseOpr.writeConfig(key, value, null);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}